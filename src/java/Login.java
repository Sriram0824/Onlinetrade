/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

/**
 *
 * @author sriram
 */
@ManagedBean
@SessionScoped
public class Login {
    
    private String userName;
    private String password;
    private String question;
    private String answer;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    
    
    
    private UserDetails details;
    private Trade userdetails;
    private Display userdetails1;

    public Display getUserdetails1() {
        return userdetails1;
    }

    public void setUserdetails1(Display userdetails1) {
        this.userdetails1 = userdetails1;
    }
    

    public Trade getUserdetails() {
        return userdetails;
    }

    public void setUserdetails(Trade userdetails) {
        this.userdetails = userdetails;
    }
    
    public UserDetails getDetails() {
        return details;
    }

    public void setDetails(UserDetails details) {
        this.details = details;
    }
    
   
    
    public Login()
    {
        
    }

    

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String ValidateUser()
    {
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            
        }
        catch (Exception e)
        {
            return (e.getMessage());
        }
       
        final String DB_URL="jdbc:mysql://mis-sql.uhcl.edu/manchirajus5432";
        Connection conn=null;
        Statement stat=null;
        ResultSet rs=null;
        
        try
        {
           conn=DriverManager.getConnection(DB_URL,"manchirajus5432","1447239");
           stat=conn.createStatement();
           
              
           rs=stat.executeQuery("select * from users where loginid='"+userName+"' and password='"+password+"' and isactive=true");
           if(rs.next())
           {
               details=new UserDetails(userName,password);
               userdetails=new Trade(userName);
               userdetails1=new Display(userName);
             //redirects the user to online home page if account is unlocked and username password matches
             return "Home";
           }
           
           else
           {
               //redirects the user back to login page if password username doesnt match
               return "index";
           }
           
          
           
        }
        
        catch(SQLException e)
        {
            
            e.printStackTrace();
            return (e.getMessage());
        }
        finally
        {
            try
            {
                conn.close();
                stat.close();
                rs.close();
                
            }
            
            catch(Exception e)
            {
                e.printStackTrace();
                return (e.getMessage());
            }
            
           
        }
        
       
        
      }
    
    public String Logout()
    {
    FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
     return "index";
}
    
    public ArrayList<String> ForgotPassword()
    {
        
        final String DB_URL="jdbc:mysql://mis-sql.uhcl.edu/manchirajus5432";
        Connection conn=null;
        Statement stat=null;
        ResultSet rs=null;
        String originalAnswer="";
        ArrayList<String> showPassword=new ArrayList<String>();
        
        
         try
             {
                 //uses random function to display one of the two security questions
                 Random random=new Random();
                 int r=random.nextInt();
                 
             
           conn=DriverManager.getConnection(DB_URL,"manchirajus5432","1447239");
           stat=conn.createStatement();
           if(r%2==0)
           {
             rs=stat.executeQuery("select * from users where loginid='sriram0824'"); 
             
             if(rs.next())
             {
                 question=rs.getString("fsq");
                 originalAnswer=rs.getString("fsqa");
                 password=rs.getString("password");
             
             
             
             
             if(originalAnswer.equals(answer))
             {
                 showPassword.add("This is Your passowrd");
                 showPassword.add(answer);
                 String sql="update users set isactive=? where loginid='"+userName+"' ";
                 PreparedStatement p=conn.prepareStatement(sql);
                 p.setBoolean(1,true);
                 p.executeUpdate();
                 return showPassword;
             }
           }  
             else
             {
                 showPassword.add("NoUserFound");
                 return showPassword;
             }
             
           }
           if(r%2==1)
           {
             rs=stat.executeQuery("select * from users where loginid='sriram0824'"); 
             
             if(rs.next())
             {
                 question=rs.getString("ssq");
                 originalAnswer=rs.getString("ssqa");
                  password=rs.getString("password");
             
             
             
             
             
             if(originalAnswer.equals(answer))
             {
                 showPassword.add("This is Your passowrd");
                 showPassword.add(answer);
                 //displays the user password and redirects to login page
                 return showPassword;
                 
             }
            
           }
             else
             {
                 showPassword.add("NoUserFound");
                 return showPassword;
             }
                
             
           }
        
           
           }
        
        catch(SQLException e)
        {
              showPassword.add(e.getMessage());
                 return showPassword;
        }
        finally
        {
            try
            {
                conn.close();
                stat.close();
                rs.close();
                
              }
            
            catch(Exception e)
            {
                  showPassword.add(e.getMessage());
                 return showPassword;
            }
              
        
       }
         
         return showPassword;
    }
        
}      
        
    
