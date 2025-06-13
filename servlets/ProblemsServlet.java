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

import java.util.Set;
import java.util.HashSet;

import database.DatabaseConnection;
import database.SQLQueries;

@WebServlet("/problems")
public class ProblemsServlet extends HttpServlet{
          
          public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException{
                    HttpSession session = request.getSession(false);
                    
                    if(session == null || session.getAttribute("user_id") == null){
                           response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in.");
                           return;
                    }
                    
                    int userId = (int)session.getAttribute("user_id");
                    response.setCharacterEncoding("UTF-8");
                    response.setContentType("application/json; charset=UTF-8");
                    
                    PrintWriter writer = response.getWriter();
                    
                    try{
                          String getAllProblems = SQLQueries.GET_ALL_PROBLEMS;
                          PreparedStatement getAllProblemsPreparedStatement = DatabaseConnection.getPreparedStatement(getAllProblems);
                          ResultSet getAllProblemsResultSet = getAllProblemsPreparedStatement.executeQuery();
                          
                          String getSolvedProblems = SQLQueries.GET_SOLVED_PROBLEMS;
                          PreparedStatement getSolvedProblemsPreparedStatement = DatabaseConnection.getPreparedStatement(getSolvedProblems);
                          getSolvedProblemsPreparedStatement.setInt(1, userId);
                          ResultSet getSolvedProblemsResultSet = getSolvedProblemsPreparedStatement.executeQuery();
                          
                          Set<Integer> solvedProblemIds = new HashSet();
                          while(getSolvedProblemsResultSet.next())
                                 solvedProblemIds.add(getSolvedProblemsResultSet.getInt("problem_id"));
                                 
                      
                        StringBuilder json = new StringBuilder();
                        json.append("[");

                        while (getAllProblemsResultSet.next()) {
                            int pid = getAllProblemsResultSet.getInt("problem_id");
                            String title = getAllProblemsResultSet.getString("title");
                            String description = getAllProblemsResultSet.getString("description").replace("\"", "\\\"");

                            json.append("{");
                            json.append("\"problem_id\":").append(pid).append(",");
                            json.append("\"title\":\"").append(title).append("\",");
                            json.append("\"description\":\"").append(description).append("\",");
                            json.append("\"solved\":").append(solvedProblemIds.contains(pid));
                            json.append("},");
                        }       
                        
                        if (json.charAt(json.length() - 1) == ',') 
                            json.setLength(json.length() - 1); // Remove trailing comma
                        
                        json.append("]");
                       writer.write(json.toString());
                    }
                   catch (SQLException e) {
                       e.printStackTrace();
                      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error fetching problems.");
                   }
          }
}
