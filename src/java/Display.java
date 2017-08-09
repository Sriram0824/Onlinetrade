/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author sriram
 */

public class Display {

    
    private String onlineId;
    
    /**
     * Creates a new instance of Display
     */
    public Display(String id) 
    {
        onlineId=id;
    }

    public String getOnlineId() {
        return onlineId;
    }

    public void setOnlineId(String onlineId) {
        this.onlineId = onlineId;
    }
   
    
    
    public ArrayList<String> DisplayOrders()
    {
        
         ArrayList<String> userOrders=new ArrayList<String>();
        ArrayList<String> error=new ArrayList<String>();
        Connection conn=null;
        Statement stat=null;
        ResultSet rs=null;
        String DB_URL="jdbc:mysql://mis-sql.uhcl.edu/manchirajus5432";
        
      //displas the users 5 latest transactions  
        try
        {
            conn=DriverManager.getConnection(DB_URL,"manchirajus5432","1447239");
            stat=conn.createStatement();
            rs=stat.executeQuery("select * from transactions where loginid='"+onlineId+"' order by transactiontime desc limit 5 ");
             //boolean recordExist=false;
             
              while(rs.next() )
            {
                
                String orders=("StockName:"+rs.getString("stockname")+" StockPrice: "+rs.getString("stockprice")+" OrderType:"+rs.getString("ordertype")+" TradeType:"+rs.getString("tradetype")+" numberofshares:"+rs.getString("numberofshares")+" orderstatus:"+rs.getString("orderstatus")+" TransactionTime:"+rs.getString("transactiontime"));
                userOrders.add(orders);
                
            }  
              
              
            
              //displays the messsage if no record exist
             if(userOrders.size()<=0)
             {
                  
                 userOrders.add("NO Orders");
                 return userOrders;
             }else{
                 return userOrders;
             }
             
            
              
            
             
            
        }
        catch(SQLException e)
        {
            
            e.printStackTrace();
            error.add(e.getMessage());
            return error;
        }
        finally
        {
            try
            {
                stat.close();
                conn.close();
                rs.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                error.add(e.getMessage());
            return error;
            }
            
        }
      
            
       
    }
    
     
}
