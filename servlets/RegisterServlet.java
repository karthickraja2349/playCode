package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;

import database.DatabaseConnection;
import database.SQLQueries;
import utils.PasswordHash;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String userName = request.getParameter("userName");
        String password = request.getParameter("userPassword");
        String gmailId = request.getParameter("emailID");

        response.setContentType("text/plain");
        PrintWriter writer = response.getWriter();

        byte[] salt = PasswordHash.generateSalt();
        String encodedSalt = PasswordHash.encodeSalt(salt);
        String hashedPassword = null;

        try {
            hashedPassword = PasswordHash.hashPassword(password, salt);
        }
        catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Password hashing failed");
            return;
        }

        try {
            boolean userExists = false;
            try {
                userExists = checkUserExistence(response, writer, userName, password);
            } 
            catch (Exception e) {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "User check failed");
                return;
            }

            if (userExists)
                return;

            String insertUser = SQLQueries.INSERT_USER;
            PreparedStatement preparedStatement = DatabaseConnection.getPreparedStatement(insertUser);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, gmailId);
            preparedStatement.setString(4, encodedSalt);

            int rowsInserted = preparedStatement.executeUpdate();

            if (rowsInserted > 0)
                writer.write("success");
            else
                writer.write("fail");
        } 
        catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Database Error: " + e.getMessage());
        }
    }

    private boolean checkUserExistence(HttpServletResponse response, PrintWriter writer,
                                       String userName, String password) throws Exception {
        try {
            String checkUserExistence = SQLQueries.CHECK_USER_EXISTENCE;
            PreparedStatement preparedStatement = DatabaseConnection.getPreparedStatement(checkUserExistence);
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                writer.write("exists");
                return true;
            }
        } 
        catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Database Error: " + e.getMessage());
        }
        return false;
    }
}

