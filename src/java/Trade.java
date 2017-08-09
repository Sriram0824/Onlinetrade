
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sriram
 */

public class Trade {
    
    private int numberofSharestoBuy;
    private int numberofSharestoSell;
    private String stockSymbol;
    private String onlineId;
    
    
    
    public Trade(String id)
    {
        onlineId=id;
    }
    
    

    public String getOnlineId() {
        return onlineId;
    }

    public void setOnlineId(String onlineId) {
        this.onlineId = onlineId;
    }

    public int getNumberofSharestoBuy() {
        return numberofSharestoBuy;
    }

    public void setNumberofSharestoBuy(int numberofSharestoBuy) {
        this.numberofSharestoBuy = numberofSharestoBuy;
    }

    public int getNumberofSharestoSell() {
        return numberofSharestoSell;
    }

    public void setNumberofSharestoSell(int numberofSharestoSell) {
        this.numberofSharestoSell = numberofSharestoSell;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }
    
    public String BuyOrders()
    {
        
        int temp=0;
        
        String stockName="";
        double stockPrice=0.00;
        Connection conn=null;
        Statement stat=null;
        ResultSet rs=null;
        String DB_URL="jdbc:mysql://mis-sql.uhcl.edu/manchirajus5432";
        boolean notBought=true;
        
        
        try
        {
            Class.forName("com.mysql.jdbc.Driver");
        }
        catch(Exception e)
        {
            return(e.getMessage());
        }
     
           try
           {
               conn=DriverManager.getConnection(DB_URL,"manchirajus5432","1447239");
               stat=conn.createStatement();
               rs=stat.executeQuery("select * from stocks where stocksymbol='"+stockSymbol+"' ");
               
               if(rs.next())
               {
               stockName=rs.getString("stockname");
               
               boolean rowExist=true;
               
               //inserting details in to orders and transaction table
               String sql="INSERT INTO orders (loginid, stocksymbol,stockname,numberofsharestobuy,orderstatus,stockpricebuy,tradetype)" +"VALUES (?, ?, ?, ?, ?, ?, ?)";
                  PreparedStatement p=conn.prepareStatement(sql);
                  p.setString(1,onlineId);
                  p.setString(2,stockSymbol);
                  p.setString(3,stockName);
                  p.setInt(4,numberofSharestoBuy); 
                  p.setString(5,"pending"); 
                  p.setDouble(6,stockPrice);
                  p.setString(7,"buy");
                  p.executeUpdate();
                  
                  
                  String sql1="INSERT INTO transactions (loginid, stocksymbol,stockname, stockprice,ordertype,tradetype,numberofshares,orderstatus)" +"VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                  PreparedStatement p1=conn.prepareStatement(sql1);
                  p1.setString(1,onlineId);
                  p1.setString(2,stockSymbol);
                  p1.setString(3,stockName);
                  p1.setDouble(4,stockPrice);
                  p1.setString(5,"market");
                  p1.setString(6,"buy");
                  p1.setInt(7,numberofSharestoBuy);
                  p1.setString(8,"pending"); 
                  
                  p1.executeUpdate();
               
               
               
               while(rowExist)
               {
                   rs=stat.executeQuery("select * from orders where stocksymbol='"+stockSymbol+"' and tradetype='sell' and orderstatus='pending' and stockpricesell=(select MIN(stockpricesell) from orders where stocksymbol='"+stockSymbol+"' and orderstatus='pending' and tradetype='sell') ");
               if(rs.next())
               {
                   notBought=false;
                  String sellersOnlineId=rs.getString("loginid");
                  int transactionStockPrice=rs.getInt("stockpricesell" );
                  int numberofSharesAvailable=rs.getInt("numberofsharestosell");
                  
                  //updating orders table of buyers information
                  String sql2="update orders set numberofsharestobuy=?,orderstatus=? where loginid='"+onlineId+"' and orderstatus='pending' and stocksymbol='"+stockSymbol+"' and tradetype='buy' " ;
                  PreparedStatement p2=conn.prepareStatement(sql2);
  
                  if(numberofSharestoBuy>numberofSharesAvailable)
                  {
                     p2.setInt(1,numberofSharestoBuy-numberofSharesAvailable);
                     p2.setString(2,"pending");
                     temp=numberofSharestoBuy-numberofSharesAvailable;
                  }
                  
                  if(numberofSharestoBuy<=numberofSharesAvailable)
                  {
                     p2.setInt(1,0); 
                     p2.setString(2,"completed");
                     temp=0;
                     rowExist=false;
                  }
                  
                  
                  p2.executeUpdate();
                  
                  //inserting buyers details into transaction tables after buy
                  String sql3="INSERT INTO transactions (loginid, stocksymbol,stockname, stockprice,ordertype,tradetype,numberofshares,orderstatus)" +"VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                  PreparedStatement p3=conn.prepareStatement(sql3);
                  p3.setString(1,onlineId);
                  p3.setString(2,stockSymbol);
                  p3.setString(3,stockName);
                  p3.setDouble(4,transactionStockPrice);
                  p3.setString(5,"market");
                  p3.setString(6,"buy");
                  p3.setInt(7,numberofSharestoBuy);
                  if(numberofSharestoBuy<=numberofSharesAvailable)
                  {
                      p3.setString(8,"completed");
                  }
                  else
                  {
                      p3.setString(8,"pending"); 
                  }
                  p3.executeUpdate();
                  
                  //updating sellers information in orders table
                   String sql4="update orders set numberofsharestosell=?,orderstatus=? where loginid='"+sellersOnlineId+"' and orderstatus='pending' and stocksymbol='"+stockSymbol+"' and tradetype='sell' ";
                   PreparedStatement p4=conn.prepareStatement(sql4);
                   if(numberofSharesAvailable>numberofSharestoBuy)
                  {
                    
                    p4.setInt(1,numberofSharesAvailable-numberofSharestoBuy);
                    p4.setString(2,"pending");
                  }
                  if(numberofSharesAvailable<=numberofSharestoBuy)
                  {
                      p4.setInt(1,0);  
                       p4.setString(2,"completed");
                  }
                  
                  p4.executeUpdate();
                  
                  //inserting sellers details in transaction table
                  String sql5="INSERT INTO transactions (loginid, stocksymbol,stockname, stockprice,ordertype,tradetype,numberofshares,orderstatus)" +"VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                  PreparedStatement p5=conn.prepareStatement(sql5);
                  p5.setString(1,sellersOnlineId);
                  p5.setString(2,stockSymbol);
                  p5.setString(3,stockName);
                  p5.setDouble(4,transactionStockPrice);
                  p5.setString(5,"market");
                  p5.setString(6,"sell");
                  if(numberofSharesAvailable>numberofSharestoBuy)
                  {
                  p5.setInt(7,numberofSharestoBuy);
                   p5.setString(8,"pending");
                  }
                  if(numberofSharesAvailable<=numberofSharestoBuy)
                  {
                      p5.setInt(7,numberofSharesAvailable);
                      p5.setString(8,"completed");
                  }
                  
                  p5.executeUpdate();
                  
                  if(numberofSharesAvailable>numberofSharestoBuy)
                  {
                  System.out.println("number of shares bought: "+numberofSharestoBuy);
                  }
                  if( numberofSharesAvailable<=numberofSharestoBuy)
                  {
                      System.out.println("number of shares bought: "+numberofSharesAvailable);
                  }
                  numberofSharestoBuy=temp; 
                  return "SharesBought";
                  
                  
                }
               
               else
               {
                   rowExist=false;
                   
                   if(notBought)
                   {
                  return "NoSellerFound";
                  
                   }
                }
             }
               
            
           }       
               else
               {
                   return "NoSymbolFound";
                   
                   
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
        return "";
    }
    
    public String SellOrders()
    {
        
          int temp=0;
          String stockName="";
        
        double stockPrice=0.00;
        Connection conn=null;
        Statement stat=null;
        ResultSet rs=null;
        String DB_URL="jdbc:mysql://mis-sql.uhcl.edu/manchirajus5432";
        boolean notSold=true;
        
          
           try
           {
               conn=DriverManager.getConnection(DB_URL,"manchirajus5432","1447239");
               stat=conn.createStatement();
               rs=stat.executeQuery("select * from stocks where stocksymbol='"+stockSymbol+"' ");
               if(rs.next())
               {
               stockName=rs.getString("stockname");
               
               boolean rowExist=true;
               
               String sql="INSERT INTO orders (loginid, stocksymbol,stockname,numberofsharestosell,orderstatus,stockpricesell,tradetype)" +"VALUES (?, ?, ?, ?, ?,?,?)";
                  PreparedStatement p=conn.prepareStatement(sql);
                  p.setString(1,onlineId);
                  p.setString(2,stockSymbol);
                  p.setString(3,stockName);
                  p.setInt(4,numberofSharestoSell); 
                  p.setString(5,"pending"); 
                  p.setDouble(6,stockPrice);
                  p.setString(7,"sell");
                  
                  p.executeUpdate();
                  
                  //inserting sellers details into transaction tables and making order status pending
                  String sql1="INSERT INTO transactions (loginid, stocksymbol,stockname, stockprice,ordertype,tradetype,numberofshares,orderstatus)" +"VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                  PreparedStatement p1=conn.prepareStatement(sql1);
                  p1.setString(1,onlineId);
                  p1.setString(2,stockSymbol);
                  p1.setString(3,stockName);
                  p1.setDouble(4,stockPrice);
                  p1.setString(5,"market");
                  p1.setString(6,"sell");
                  p1.setInt(7,numberofSharestoSell);
                  p1.setString(8,"pending"); 
                  
                  p1.executeUpdate();
               
               
            while(rowExist)
               {
                   rs=stat.executeQuery("select * from orders where stocksymbol='"+stockSymbol+"' and tradetype='buy' and orderstatus='pending' and stockpricebuy=(select MAX(stockpricebuy) from orders where orderstatus='pending' and stocksymbol='"+stockSymbol+"' and tradetype='buy') ");
                 if(rs.next())
                     
                 {
                     notSold=false;
                  String buyersOnlineId=rs.getString("loginid");
                  double transactionStockPrice=rs.getDouble("stockpricebuy");
                  int numberofSharesAvailable=rs.getInt("numberofsharestobuy");
                 
                     
                  //updating sellers details in orders table
                  String sql2="update orders set numberofsharestosell=?,orderstatus=? where loginid='"+onlineId+"' and orderstatus='pending' and stocksymbol='"+stockSymbol+"' and tradetype='sell' " ;
                  PreparedStatement p2=conn.prepareStatement(sql2);
                  
                  if(numberofSharestoSell>numberofSharesAvailable)
                  {
                     p2.setInt(1,numberofSharestoSell-numberofSharesAvailable);
                      p2.setString(2,"pending");
                      temp=numberofSharestoSell-numberofSharesAvailable;
                  }
                  
                  if(numberofSharestoSell<=numberofSharesAvailable)
                  {
                     p2.setInt(1,0);
                     p2.setString(2,"completed");
                     temp=0;
                     rowExist=false;
                  }
                  
                  
                 p2.executeUpdate();
                  
                  //inserting sellers details into transaction tables 
                  String sql3="INSERT INTO transactions (loginid, stocksymbol,stockname, stockprice,ordertype,tradetype,numberofshares,orderstatus)" +"VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                  PreparedStatement p3=conn.prepareStatement(sql3);
                  p3.setString(1,onlineId);
                  p3.setString(2,stockSymbol);
                  p3.setString(3,stockName);
                  p3.setDouble(4,transactionStockPrice);
                  p3.setString(5,"market");
                  p3.setString(6,"sell");
                  if(numberofSharestoSell>numberofSharesAvailable)
                  {
                  p3.setInt(7,numberofSharesAvailable);
                  p3.setString(8,"pending"); 
                  }
                  if(numberofSharestoSell<=numberofSharesAvailable)
                  {
                      p3.setInt(7,numberofSharestoSell);
                      p3.setString(8,"completed");
                  }
                  
                  
                  p3.executeUpdate();
                  
                  
                  //updating buyers information in orders table
                   String sql4="update orders set numberofsharestobuy=?,orderstatus=? where loginid='"+buyersOnlineId+"' and stocksymbol='"+stockSymbol+"' and orderstatus='pending' ";
                   PreparedStatement p4=conn.prepareStatement(sql4);
                   if(numberofSharesAvailable>numberofSharestoSell)
                  {
                    
                    p4.setInt(1,numberofSharesAvailable-numberofSharestoSell);
                    p4.setString(2,"pending");
                  }
                  if(numberofSharesAvailable <=numberofSharestoSell)
                  {
                      p4.setInt(1,0); 
                       p4.setString(2,"completed");
                  }
                  
                  p4.executeUpdate();
                  
                  //inserting buyers details in transaction table
                  String sql5="INSERT INTO transactions (loginid, stocksymbol,stockname, stockprice,ordertype,tradetype,numberofshares,orderstatus)" +"VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                  PreparedStatement p5=conn.prepareStatement(sql5);
                  p5.setString(1,buyersOnlineId);
                  p5.setString(2,stockSymbol);
                  p5.setString(3,stockName);
                  p5.setDouble(4,transactionStockPrice);
                  p5.setString(5,"market");
                  p5.setString(6,"buy");
                  if(numberofSharesAvailable>numberofSharestoSell)
                  {
                  p5.setInt(7,numberofSharestoSell);
                  }
                  if(numberofSharesAvailable<=numberofSharestoSell)
                  {
                      p5.setInt(7,numberofSharesAvailable);
                  }
                  
                  if(numberofSharesAvailable<=numberofSharestoSell)
                  {
                      p5.setString(8,"completed");
                  }
                  else
                  {
                      p5.setString(8,"pending"); 
                  }
                  p5.executeUpdate();
                  
                  if(numberofSharesAvailable>numberofSharestoSell)
                  {
                  System.out.println("Number of Shares sold: "+numberofSharestoSell);
                  }
                  if(numberofSharesAvailable<=numberofSharestoSell)
                  {
                      System.out.println("Number of Shares sold: "+numberofSharesAvailable);
                  }
                 
                 numberofSharestoSell=temp;
                 return "SharesSold";
                  
                  
                }
               
               else
               {
                   //if no buyer found 
                   rowExist=false;
                   if(notSold)
                   {
                      return "NoBuyerFound";
                   }
                       
                }
            }      
              
                
        }      
               else
               {
                   return "NoSymbolFound";
                   
               }
               
           }
    
           
           catch(SQLException e)
            {
                       
                      e.printStackTrace();
                      return "InternalError";
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
                   return "InternalError";
               }
           }
       return "";
    }
}
