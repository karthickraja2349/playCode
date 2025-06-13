package servlets;

import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.OutputStream;

import java.nio.file.Files;
import java.nio.file.Paths;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import database.DatabaseConnection;
import database.SQLQueries;

@WebServlet("/submitServlet")
public class SubmitServlet extends HttpServlet {

    private static final String BASE_DIR = "/home/ayyappankalai/Server/apache-tomcat-10.1.34/webapps/playCode/user_submissions";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        String code = "";
        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();
        
        if (session == null) {
            response.getWriter().write("Session not found. Please login.");
            return;
        }

        Integer userId = (Integer) session.getAttribute("user_id");
        if (userId == null) {
            response.getWriter().write("User not logged in.");
            return;
        }

        String problemId = request.getParameter("problemId");
        String folderName = "user_" + userId + "_problem_" + problemId;
        File fileDirectory = new File(BASE_DIR + "/" + folderName);

        if (!fileDirectory.exists()) {
            response.getWriter().write("Run your code first before submitting.");
            return;
        }

        try {
            String hiddenQuery = SQLQueries.GET_HIDDEN_TESTCASES;
            PreparedStatement getHiddenTestcasesPreparedStatement = DatabaseConnection.getPreparedStatement(hiddenQuery);
            getHiddenTestcasesPreparedStatement.setString(1, problemId);
            ResultSet getHiddenTestcasesResultSet = getHiddenTestcasesPreparedStatement.executeQuery();

            int testNum = 0;
            boolean allPassed = true;
            StringBuilder result = new StringBuilder();

            while (getHiddenTestcasesResultSet.next()) {
                testNum++;
                String input = getHiddenTestcasesResultSet.getString("input");
                String expectedOutput = getHiddenTestcasesResultSet.getString("expected_output").trim();
/*
                // ✅ Run already compiled Main.class with new input
                Process runProcess = new ProcessBuilder("java", "Main")
                        .directory(fileDirectory)
                        .redirectErrorStream(true)
                        .start();

                // Provide input to Scanner via stdin
                OutputStream stdin = runProcess.getOutputStream();
                stdin.write(input.getBytes());
                stdin.flush();
                stdin.close();
*/
                String userCode = Files.readString(Paths.get(fileDirectory.getPath(), "raw.txt"));
                code = userCode;
                String fullCode = wrapCode(userCode, input);
                
                File javaFile = new File(fileDirectory, "Main.java");
                 try (FileWriter fileWriter = new FileWriter(javaFile)) {
                      fileWriter.write(fullCode);
                  }
                  
                  Process compileProcess = new ProcessBuilder("javac", "Main.java")
                                                      .directory(fileDirectory)
                                                      .redirectErrorStream(true)
                                                      .start();
                                                      
                                                      
             
                  String compileOutput = readStream(compileProcess.getInputStream());
                  compileProcess.waitFor();
                  if (compileProcess.exitValue() != 0) {
                      response.getWriter().write("Compilation failed:\n" + compileOutput);
                      return;
                  }
                  
                  Process runProcess = new ProcessBuilder("java", "Main")
                    .directory(fileDirectory)
                    .redirectErrorStream(true)
                    .start();

            String runOutput = readStream(runProcess.getInputStream()).trim();
            runProcess.waitFor();

                if (!runOutput.equals(expectedOutput)) {
                    result.append("Test Case ").append(testNum).append(" Failed:\n")
                          .append("Input: ").append(input).append("\n")
                          .append("Expected: ").append(expectedOutput).append("\n")
                          .append("Got: ").append(runOutput).append("\n\n");
                    allPassed = false;
                } 
                else {
                    result.append("Test Case ").append(testNum).append(" Passed\n");
                }
            }

            
            writer.write(allPassed ? "All hidden test cases passed. Code submitted successfully!\n" + result : result.toString());
            
            if(allPassed)
                storeSolution(userId, problemId,code, writer);

        } 
        catch (Exception e) {
      //      e.printStackTrace();
      //     writer.write("Server error: " + e.getMessage());
        }
    }

    private String readStream(InputStream stream) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        StringBuilder out = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            out.append(line).append("\n");
        }
        return out.toString();
    }
    
    private String wrapCode(String userCode, String input) {
          input = input.replace("\\", "\\\\").replace("\"", "\\\"");

          StringBuilder sb = new StringBuilder();
          sb.append("import java.util.*;\n");
          sb.append("public class Main {\n");
          sb.append("    public static void main(String[] args) {\n");
          sb.append("        Scanner sc = new Scanner(\"").append(input).append("\");\n");
          sb.append("        Solution solution = new Solution();\n");
          sb.append("        solution.solve(sc);\n");
          sb.append("    }\n");
          sb.append("}\n\n");
          sb.append("class Solution {\n");
          sb.append(userCode).append("\n");
          sb.append("}\n");

          return sb.toString();
      }
      
      private void storeSolution(int userId, String problemId, String userCode, PrintWriter writer){
          
          try{
              String insertSubmission = SQLQueries.INSERT_SUBMISSIONS;
              PreparedStatement getInsertSubmissionPreparedStatement = DatabaseConnection.getPreparedStatement(insertSubmission);
              getInsertSubmissionPreparedStatement.setInt(1, userId);
              getInsertSubmissionPreparedStatement.setInt(2, Integer.parseInt(problemId));
              getInsertSubmissionPreparedStatement.setString(3, userCode);
              getInsertSubmissionPreparedStatement.setString(4, "PASSED");
              getInsertSubmissionPreparedStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
              
              int rowsInserted = getInsertSubmissionPreparedStatement.executeUpdate();
              
              String markSolvedProblems = SQLQueries.MARK_SOLVED_PROBLEMS;
              PreparedStatement getMarkSolvedPreparedStatement =  DatabaseConnection.getPreparedStatement(markSolvedProblems);
              getMarkSolvedPreparedStatement.setInt(1, userId);
              getMarkSolvedPreparedStatement.setInt(2, Integer.parseInt(problemId));
              getMarkSolvedPreparedStatement.setTimestamp(3,new Timestamp(System.currentTimeMillis()));
              
              getMarkSolvedPreparedStatement.executeUpdate();
              
              if(rowsInserted != 0)
                    writer.write("submission Accepted");
              else
                    writer.write("Sorry submission Not Accept at this time due to Server issue");
          }    
          catch(SQLException e){
    //         e.printStackTrace();
        //     writer.write("Server error: " + e.getMessage());
          }
      }

}

