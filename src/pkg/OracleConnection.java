package pkg;

import java.sql.Connection;
//import java.sql.Date;
import java.sql.DriverManager;
//import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Statement;
import java.text.ParseException;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.time.format.*;
//import java.util.Locale;
import java.util.Date;
import java.text.SimpleDateFormat;
//import java.util.Locale;
//import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OracleConnection {

	public static final String DBURL = "jdbc:oracle:thin:@172.16.7.243:1521:NDCLD";
	public static final String DBUSER = "xxnt";
	public static final String DBPASS = "xxnt";

	public static void main(String[] args) throws SQLException, ParseException {

		Dollar dollar = new Dollar();
		dollar.parseNRB();

		Date lastDate = null;

		// date extracted from site is string so formatted and converted to sql date
		// type
		// java6
		System.out.println(dollar.getExchangeDate());
		// String s = dollar.getExchangeDate();
		SimpleDateFormat formatter1 = new SimpleDateFormat("MMMM d, yyyy");
		java.util.Date siteDate = formatter1.parse(dollar.getExchangeDate());
		java.sql.Date sqlDate = new java.sql.Date(siteDate.getTime());

		// java 8
		// DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy",
		// Locale.ENGLISH);
		// LocalDate siteDateString = LocalDate.parse(dollar.getExchangeDate(),
		// formatter);
		// Date siteDate = java.sql.Date.valueOf(siteDateString);

		// Load Oracle JDBC Driver
		DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

		// Connect to Oracle Database
		Connection con = DriverManager.getConnection(DBURL, DBUSER, DBPASS);

		// Statement statement = con.createStatement();
		// Prepared statement is used over statement due to various advantage (see
		// sites)
		// so PreparedStatement used in both insert and update.
		PreparedStatement selectStatement = con.prepareStatement(
				"select * from (select * from xxnt.xxnt_gl_daily_rates_stg order by creation_date desc) where rownum=?");

		selectStatement.setString(1, "1");
		ResultSet rs = selectStatement.executeQuery();
		// ResultSet rs = statement.executeQuery("select * from (SELECT * FROM
		// XXNT.XXNT_USD_EX_RATE order by updated_date desc) where rownum=1");
		// PreparedStatement pstmt = con.prepareStatement("SELECT * FROM
		// XXNT.XXNT_USD_EX_RATE order by updated_date desc) where rownum=1");

		if (rs.next()) {
			lastDate = rs.getDate(4);
			System.out.println("last inserted date in system " + lastDate);
		}

		rs.close();
		selectStatement.close();

		if (siteDate.equals(lastDate)) {
			System.exit(0);
			// }else {System.out.println("need to insert");}
		} else {
			/*
			 * PreparedStatement pstmt =
			 * con.prepareStatement("INSERT INTO xxnt.xxnt_gl_daily_rates_stg\n" +
			 * "             (from_currency, \n" + "              to_currency, \n" +
			 * "              from_date, \n" + "              end_date, \n" +
			 * "              conv_type, \n" + "              exchange_rate, \n" +
			 * "              status,\n" + "              error_message,\n" +
			 * "              created_by,\n" + "              creation_date \n" +
			 * "             )\n" + "       VALUES \n" + "            ('USD',\n" +
			 * "             'NPR',\n" + "             ?,\n" + "             ?,\n" +
			 * "             'NT_Sell',\n" + "             ?,\n" + "             'N',\n" +
			 * "             ?,\n" + "             'JAVA_App',\n" + "             ?\n" +
			 * "            )");
			 */

			PreparedStatement pstmt = con.prepareStatement("INSERT INTO xxnt.xxnt_gl_daily_rates_stg "
					+ "(from_currency,to_currency,from_date,end_date,conv_type,exchange_rate,status,error_message) VALUES "
					+ "(?,?,?,?,?,?,?,?)");

			// pstmt.setInt(1,1);
			pstmt.setString(1, "USD");
			pstmt.setString(2, "NPR");
			pstmt.setDate(3, sqlDate);
			// java 8
			// pstmt.setDate(3,(java.sql.Date) siteDate);
			pstmt.setDate(4, sqlDate);
			pstmt.setString(5, "NT_Sell");
			pstmt.setDouble(6, dollar.getSelling_usd());
			pstmt.setString(7, "N");
			pstmt.setString(8, "Data inserted Successfully");
			// pstmt.setString(9, "Java_App");
			// pstmt.setTimestamp(10,getCurrentTimeStamp());
			// pstmt.setDouble(3,dollar.getBuying_usd());
			// pstmt.setTimestamp(4,getCurrentTimeStamp());
			// pstmt.setString(4, "Data inserted Successfully");
			// pstmt.setDate(5,(java.sql.Date) siteDate);
			// pstmt.setTimestamp(5,getCurrentTimeStamp());

			// System.out.println("executed till here");

			pstmt.execute();
			System.out.println("Date inserted successfully");
			pstmt.close();
		}

		// Statement statement = con.createStatement();

		// Execute a SELECT query on Oracle Dummy DUAL Table. Useful for retrieving
		// system values
		// Enables us to retrieve values as if querying from a table
		// ResultSet rs = statement.executeQuery("SELECT SYSDATE FROM DUAL");
		// ResultSet rs = statement.executeQuery("SELECT * FROM XXNT.XXNT_USD_EX_RATE");

		/*
		 * if (rs.next()) { double currentDate = rs.getDouble(3); // get first column
		 * returned System.out.println("Current Date from Oracle is : "+currentDate); }
		 * rs.close(); statement.close();
		 */
		con.close();

	}
	/*
	 * private static java.sql.Timestamp getCurrentTimeStamp() { java.util.Date
	 * today = new java.util.Date(); return new java.sql.Timestamp(today.getTime());
	 * }
	 */

}
