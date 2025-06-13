package database;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class DatabaseConnection {
    private static Connection connection=null;
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/playCode";
    private static final String DB_USERNAME = "main";
    private static final String DB_PASSWORD = "zoho";
    
    private DatabaseConnection() { 
    
    }
    public static Connection getConnection() {
        if (connection == null) {
          try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(DB_URL,DB_USERNAME,DB_PASSWORD);
            System.out.println("jdbc was connected successfully");
          } 
         catch (SQLException | ClassNotFoundException e) {
            System.out.println("Failed to register JDBC driver: " + e.getMessage());
        }
       }
       return connection;
    }
    
    
    public static void closeConnection(){
       if(connection!=null){
         try{
           connection.close();                                 
           connection = null;                              
         }
         catch(SQLException e){
           System.out.println("Failed to close connection");
         }
      }
   }      
   
   public static PreparedStatement getPreparedStatement(String query) {
        PreparedStatement statement = null;
        try {
            Connection connection = DatabaseConnection.getConnection();
            statement = connection.prepareStatement(query);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return statement;
    }
   
   public static void main(String[]args){
          getConnection();
   }
           
            
           
}


