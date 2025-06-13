package servlets;

import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import database.DatabaseConnection;
import database.SQLQueries;

@WebServlet("/RunCodeServlet")
public class RunCodeServlet extends HttpServlet {

    private static final String BASE_DIR = "/home/ayyappankalai/Server/apache-tomcat-10.1.34/webapps/playCode/user_submissions";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        Integer userId = (Integer) session.getAttribute("user_id");

        if (userId == null) {
            response.getWriter().write("User not logged in.");
            return;
        }

        String userCode = request.getParameter("code");
        String problemId = request.getParameter("problemId");

        String folderName = "user_" + userId + "_problem_" + problemId;
        File fileDirectory = new File(BASE_DIR + "/" + folderName);
        if (!fileDirectory.exists()) 
            fileDirectory.mkdirs();
            
       

        try{
            String getVisibleTestcases = SQLQueries.GET_VISIBLE_TESTCASES;
            PreparedStatement getVisibleTestcasespreparedStatement = DatabaseConnection.getPreparedStatement(getVisibleTestcases);
            getVisibleTestcasespreparedStatement.setString(1, problemId);
            ResultSet getVisibleTestcasesResultSet = getVisibleTestcasespreparedStatement.executeQuery();

            int testNum = 0;
            boolean allPassed = true;
            StringBuilder result = new StringBuilder();

            while (getVisibleTestcasesResultSet.next()) {
                testNum++;
                String input = getVisibleTestcasesResultSet.getString("input");
                String expectedOutput = getVisibleTestcasesResultSet.getString("expected_output").trim();

                String fullCode = wrapCode(userCode, input);
             
                File javaFile = new File(fileDirectory, "Main.java");
                try (FileWriter fileWriter = new FileWriter(javaFile)) {
                    fileWriter.write(fullCode);
                }
                
                FileWriter raw = new FileWriter(new File(fileDirectory, "raw.txt"));
                raw.write(userCode);
                raw.close();

                // Compile
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

                // Run
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

            response.setContentType("text/plain");
            PrintWriter writer = response.getWriter();
            
            writer.write(allPassed ? "All test cases passed!\n" + result : result.toString());
        } 
        catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Server error: " + e.getMessage());
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
    sb.append("        solution.solve(sc);\n");  // Pass scanner to user's method
    sb.append("    }\n");
    sb.append("}\n\n");
    sb.append("class Solution {\n");
    sb.append(userCode).append("\n");
    sb.append("}\n");

    return sb.toString();
}




}

