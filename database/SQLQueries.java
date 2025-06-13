package database;

public class SQLQueries {

    public static final String INSERT_USER = "INSERT INTO users (username, password,email,password_salt) VALUES (?, ?, ?, ?)";
    
    public static final String CHECK_USER_EXISTENCE = "SELECT * FROM users WHERE userName = ? OR email = ?";
    
    public static final String VERIFY_USER = "SELECT user_id, password, password_salt FROM users WHERE userName = ?";
  
   public static final String GET_ALL_PROBLEMS =  "SELECT problem_id, title, description FROM problems";

   public static final String GET_SOLVED_PROBLEMS =  "SELECT problem_id FROM solved_problems WHERE user_id = ?";
   
   public static final String GET_QUESTION = "SELECT title, description FROM problems WHERE problem_id = ?";

   public static final String GET_TESTCASES  = "SELECT input, expected_output FROM testcases WHERE problem_id = ? AND is_hidden = false";
   
   public static final String GET_VISIBLE_TESTCASES = "SELECT input, expected_output FROM testcases WHERE problem_id = ? AND is_hidden=0";
   
   public static final String GET_HIDDEN_TESTCASES = "SELECT input, expected_output FROM testcases WHERE problem_id = ? AND is_hidden=1";
   
   public static final String MARK_SOLVED_PROBLEMS = "INSERT INTO solved_problems(user_id, problem_id, solved_time)Values(?,?,?)"; 
   
   public static final String INSERT_SUBMISSIONS = 
                    "INSERT INTO submissions(user_id,problem_id,code,status,submission_time)VALUES (?, ?, ?, ?, ?)";
                    
   public static final String GET_SUBMISSIONS = 
          "SELECT code,status,submission_time FROM submissions WHERE user_id=? AND problem_id=? ORDER BY submission_time DESC";                 
   
    
    
}

