package pkg;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class CompareDate {
	
	String erpSystemDate;
	
	public String getErpSystemDate() {
		return erpSystemDate;
	}


	public void setErpSystemDate(String erpSystemDate) {
		this.erpSystemDate = erpSystemDate;
	}


	public static final String DBURL = "jdbc:oracle:thin:@172.16.7.243:1521:NDCLD";
    public static final String DBUSER = "xxnt";
    public static final String DBPASS = "xxnt";

    
    //private static DataSource dataSource;
    
 // Prepare to insert new fields in the table
    

	public void getResult() throws SQLException, ParseException {
		
		Dollar dollar = new Dollar();
		dollar.parseNRB();
		
		
		//updated date is in string
		//convert into date
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH);
        LocalDate ldate = LocalDate.parse(dollar.getExchangeDate(), formatter);
		Date date = java.sql.Date.valueOf(ldate);
        System.out.println(date);
        
        
		// Load Oracle JDBC Driver
        DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
         
        // Connect to Oracle Database
        Connection con = DriverManager.getConnection(DBURL, DBUSER, DBPASS);
        Statement statement = con.createStatement();
        
       // PreparedStatement pstmt = con.prepareStatement("INSERT INTO XXNT.XXNT_USD_EX_RATE(ID,SELLING_USD,BUYING_USD,UPDATED_DATE) values (?,?,?,?)");
        
        ResultSet rs = statement.executeQuery("select * from (SELECT * FROM XXNT.XXNT_USD_EX_RATE order by updated_date desc) where rownum=1");
        
         if(rs.next()) {
        Date lastDate = rs.getDate(4);
        setErpSystemDate(lastDate.toString());
        System.out.println("last updated date is "+lastDate);
         }
         
         rs.close();
         statement.close();
        
        
     
 
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
	

	public boolean compareSystemNSiteDate(Date siteDate) throws ParseException {
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-mm-dd");
		Date sysDate = simpleDateFormat.parse(getErpSystemDate());
		if(sysDate.equals(siteDate))
			return true;
		else
			return false;
		
	}
}
