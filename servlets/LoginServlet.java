package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;

import database.DatabaseConnection;
import database.SQLQueries;
import utils.PasswordHash;

@WebServlet("/login")
public class LoginServlet extends HttpServlet{
         
         public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
                    
                    String userName = request.getParameter("userName");
                    String password  = request.getParameter("password");
                    
                    response.setContentType("text/plain");
                    PrintWriter writer = response.getWriter();
                    
                    try{
                         String verifyUser = SQLQueries.VERIFY_USER;
                         PreparedStatement preparedStatement = DatabaseConnection.getPreparedStatement(verifyUser);
                         preparedStatement.setString(1, userName);
                         
                         ResultSet resultSet = preparedStatement.executeQuery();
                         
                         if(resultSet.next()){
                                int userId = resultSet.getInt("user_id");
                                String storedHashPassword = resultSet.getString("password");
                                String saltedPassword = resultSet.getString("password_salt");
                                
                                byte[] saltBytes = PasswordHash.decodeSalt(saltedPassword);
                                String hashedPassword = PasswordHash.hashPassword(password, saltBytes);
                                
                                 if (hashedPassword.equals(storedHashPassword)) {
                                    HttpSession session = request.getSession();
                                    session.setAttribute("user_id", userId);
                                    session.setAttribute("userName", userName);
                                    session.setMaxInactiveInterval(30 * 60); 
                                    writer.write("success");
                                }
                                else {
                                    writer.write("invalid");
                               }
                         }
                         else {
                            writer.write("not_found");  
                         }
                   }   
                   catch (Exception e) {
                        e.printStackTrace();
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                      "Login failed due to server error: " + e.getMessage());
                   }
         }
}
