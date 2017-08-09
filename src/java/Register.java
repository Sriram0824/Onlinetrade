
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sriram
 */
@ManagedBean
@RequestScoped
public class Register {
    
    private String ssn;
    private String firstName;
    private String lastName;
    private String loginId;
    private String password;
    private String sq1;
    private String sq2;
    private String asq1;
    private String asq2;
    

    public Register()
    {
        
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSq1() {
        return sq1;
    }

    public void setSq1(String sq1) {
        this.sq1 = sq1;
    }

    public String getSq2() {
        return sq2;
    }

    public void setSq2(String sq2) {
        this.sq2 = sq2;
    }

    public String getAsq1() {
        return asq1;
    }

    public void setAsq1(String asq1) {
        this.asq1 = asq1;
    }

    public String getAsq2() {
        return asq2;
    }

    public void setAsq2(String asq2) {
        this.asq2 = asq2;
    }


    public String RegisterUser()
    {
       final String DB_URL="jdbc:mysql://mis-sql.uhcl.edu/manchirajus5432";
       Connection conn=null;
       Statement stat=null;
       ResultSet rs=null;
       double balance=0.00;
       int accountNumber=0;
       
       try
        {
            Class.forName("com.mysql.jdbc.Driver");
            
        }
        catch (Exception e)
        {
            return ("InternalError");
        }
       
        try
       {
           
         conn=DriverManager.getConnection(DB_URL,"manchirajus5432","1447239");
         stat=conn.createStatement();
         
         String sql1 = "INSERT INTO users (ssn, lastname, firstname,balance,loginid,password,isactive,fsq,fsqa,ssq,ssqa)" +"VALUES (?, ?, ?, ?, ?, ?,?,?,?,?,?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql1);
        preparedStatement.setString(1, ssn);
        preparedStatement.setString(2, lastName);
        preparedStatement.setString(3, firstName);
        preparedStatement.setDouble(4, balance);
        preparedStatement.setString(5,loginId );
        preparedStatement.setString(6,password );
        preparedStatement.setBoolean(7,true);
        preparedStatement.setString(8, sq1);
        preparedStatement.setString(9, asq1);
        preparedStatement.setString(10, sq2);
        preparedStatement.setString(11, asq2);
       
        preparedStatement.executeUpdate();
        
        return("Success");
       }
       
       catch(SQLException e)
       {
           e.printStackTrace();
           return(e.getMessage());
           
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
               return(e.getMessage());
               
               
           }
       }
       
        
    }
 }
