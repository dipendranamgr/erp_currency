package pkg;

import java.sql.Connection;
//import java.sql.Date;
import java.sql.DriverManager;
//import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Date;
//import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;




public class OracleConnection {
	
	public static final String DBURL = "jdbc:oracle:thin:@172.16.7.243:1521:NDCLD";
    public static final String DBUSER = "xxnt";
    public static final String DBPASS = "xxnt";
    
    

    
    //private static DataSource dataSource;
    
 // Prepare to insert new fields in the table
    
    
    //public Preparedstatement 

	public static void main(String[] args) throws SQLException, ParseException {
		
		Dollar dollar = new Dollar();
		dollar.parseNRB();
		
		Date lastDate = null;
		
		
		//updated date is in string
		//convert into date
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
        LocalDate siteDateString = LocalDate.parse(dollar.getExchangeDate(), formatter);
		Date siteDate = java.sql.Date.valueOf(siteDateString);
        System.out.println(siteDate);
        
        
		// Load Oracle JDBC Driver
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
         
        // Connect to Oracle Database
        Connection con = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
        
        Statement statement = con.createStatement();
        
        ResultSet rs = statement.executeQuery("select * from (SELECT * FROM XXNT.XXNT_USD_EX_RATE order by updated_date desc) where rownum=1");
        
        if(rs.next()) {
       lastDate = rs.getDate(4);       
       System.out.println("last inserted date in system "+lastDate);
        }
        
        rs.close();
        statement.close();
        
        if(siteDate.equals(lastDate)) {
        	System.exit(0);
        }else {
        	PreparedStatement pstmt = con.prepareStatement("INSERT INTO XXNT.XXNT_USD_EX_RATE(ID,SELLING_USD,BUYING_USD,UPDATED_DATE) values (?,?,?,?)");
            
            pstmt.setInt(1,1);
            pstmt.setDouble(2, dollar.getSelling_usd());
            pstmt.setDouble(3,dollar.getBuying_usd());
            //pstmt.setTimestamp(4,getCurrentTimeStamp());
            pstmt.setDate(4,(java.sql.Date) siteDate);
            
            pstmt.execute();
            System.out.println("Date inserted successfully");
        }
        
        
 
        //Statement statement = con.createStatement();
 
        // Execute a SELECT query on Oracle Dummy DUAL Table. Useful for retrieving system values
        // Enables us to retrieve values as if querying from a table
        //ResultSet rs = statement.executeQuery("SELECT SYSDATE FROM DUAL");
        //ResultSet rs = statement.executeQuery("SELECT * FROM XXNT.XXNT_USD_EX_RATE");
         
         
        /*if (rs.next()) {
            double currentDate = rs.getDouble(3); // get first column returned
            System.out.println("Current Date from Oracle is : "+currentDate);
        }
        rs.close();
        statement.close();*/
        con.close();

	}
	
	private static java.sql.Timestamp getCurrentTimeStamp() {
	    java.util.Date today = new java.util.Date();
	    return new java.sql.Timestamp(today.getTime());
	}

}

