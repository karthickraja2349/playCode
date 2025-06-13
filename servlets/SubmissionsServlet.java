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


@WebServlet("/SubmissionsServlet")
public class SubmissionsServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        
        if (session == null || session.getAttribute("user_id") == null) {
            response.getWriter().write("Not logged in.");
            return;
        }

        int userId = (int) session.getAttribute("user_id");
        String problemId = request.getParameter("problemId");

        String sql = SQLQueries.GET_SUBMISSIONS;

        try {
            PreparedStatement getSubmissionsPreparedStatement = DatabaseConnection.getPreparedStatement(sql);
            getSubmissionsPreparedStatement.setInt(1, userId);
            getSubmissionsPreparedStatement.setInt(2, Integer.parseInt(problemId));
            ResultSet getSubmissionsResultSet = getSubmissionsPreparedStatement.executeQuery();

            StringBuilder sb = new StringBuilder();
            int count = 1;
            while (getSubmissionsResultSet.next()) {
                sb.append("➤ Submission #").append(count++).append("\n")
                  .append("Status: ").append(getSubmissionsResultSet.getString("status")).append("\n")
                  .append("Time: ").append(getSubmissionsResultSet.getTimestamp("submission_time")).append("\n")
                  .append("Code:\n").append(getSubmissionsResultSet.getString("code")).append("\n")
                  .append("--------------------------------------------------\n");
            }

            if (count == 1) {
                sb.append("No submissions yet.");
            }

            response.setContentType("text/plain");
            response.getWriter().write(sb.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("Server error: " + e.toString());
        }
    }
}

