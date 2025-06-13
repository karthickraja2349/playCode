package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import database.DatabaseConnection;
import database.SQLQueries;

@WebServlet("/problemDetails")
public class ProblemDetailsServlet extends HttpServlet{
          
          protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
          
                  String problemId = request.getParameter("pid");
                  if (problemId == null) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing problem ID");
                        return;
                 }
                 
                 int pid = Integer.parseInt(problemId);
                 response.setContentType("application/json");
                 PrintWriter writer = response.getWriter();
                 
                 try{
                      String getQuestion = SQLQueries.GET_QUESTION;
                      PreparedStatement getQuestionPreparedStatement = DatabaseConnection.getPreparedStatement(getQuestion);
                      getQuestionPreparedStatement.setInt(1, pid);
                      ResultSet getQuestionResultSet = getQuestionPreparedStatement.executeQuery();
                      
                        if (!getQuestionResultSet.next()) {
                            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Problem not found");
                            return;
                        }
                        
                      String title = escapeJson(getQuestionResultSet.getString("title"));
                      String description = escapeJson(getQuestionResultSet.getString("description"));
                        
                        String getTestcase = SQLQueries.GET_TESTCASES;
                        PreparedStatement getTestcasePreparedStatement = DatabaseConnection.getPreparedStatement(getTestcase);
                        getTestcasePreparedStatement.setInt(1, pid);
                        ResultSet getTestcaseResultSet = getTestcasePreparedStatement.executeQuery();
                        
                        StringBuilder json = new StringBuilder();
                        json.append("{");
                        json.append("\"title\":\"").append(title).append("\",");
                        json.append("\"description\":\"").append(description).append("\",");
                        json.append("\"testcases\":[");
                         
                      while (getTestcaseResultSet.next()) {
                          json.append("{")
                              .append("\"input\":\"").append(escapeJson(getTestcaseResultSet.getString("input"))).append("\",")
                              .append("\"output\":\"").append(escapeJson(getTestcaseResultSet.getString("expected_output"))).append("\"")
                              .append("},");
                      }
                        if (json.charAt(json.length() - 1) == ',')
                               json.setLength(json.length() - 1);
                       json.append("]}");
                      writer.write(json.toString());
                 }
                 catch (SQLException e) {
                        e.printStackTrace();
                        
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
                 }
          }
          
          private String escapeJson(String str) {
                if (str == null) return "";
                return str.replace("\\", "\\\\")
                          .replace("\"", "\\\"")
                          .replace("\n", "\\n")
                          .replace("\r", "\\r");
            }
}
