package servlets;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.annotation.WebServlet;

import jakarta.mail.Session;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Message;
import jakarta.mail.Transport;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.InternetAddress;

import java.util.Properties;

@WebServlet("/mail")
public class MailServlet extends HttpServlet{
       
       protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        
                    String user = request.getParameter("userName");
                    String gmailId = request.getParameter("emailID");
                    response.setContentType("text/plain");
                    PrintWriter writer = response.getWriter();
                    
                    final String userName = "karthickraja.k@zsgs.in";
                    final String password =  System.getenv("ZOHOMAIL_PASSWORD");
                    
                    if(password == null){
                         throw new RuntimeException("ZOHOMAIL_PASSWORD environment variable is not set.");
                    }
                    
                    Properties properties = new Properties();
                    properties.put("mail.smtp.auth", "true");
                    properties.put("mail.smtp.starttls.enable", "true");
                    properties.put("mail.smtp.host", "smtp.zoho.com");
                    properties.put("mail.smtp.port",  "587");
                    
                    Session session = Session.getInstance(properties,  new Authenticator() {
                                                    protected PasswordAuthentication getPasswordAuthentication(){
                                                           return new PasswordAuthentication(userName, password);
                                                    }
                                                });
                   
                   try{
                         Message message = new MimeMessage(session);
                         message.setFrom(new InternetAddress(userName));
                         
                         message.setRecipients(
                               Message.RecipientType.TO,
                               InternetAddress.parse(gmailId)
                         );
                          message.setSubject("Welcome Message from PlayCode");
                          message.setText("Hello "+ user +",\n\n"+ ":We are very Excited to be working with you! Stay Tuned for Future updates");
                        
                          Transport.send(message);
                   }
                   catch (MessagingException e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                  }          
       }
}
