package mineDatabase;

import java.sql.*;
import javax.swing.JEditorPane;
import javax.swing.JFrame;

public class CreateMineDatabase {
	
	public static void main(String args[]) {
		System.out.println("Creating Mining Town Database");
		String url = "jdbc:derby:coalCamps;create=true";
		try (Connection conn = DriverManager.getConnection(url);
			 Statement stmt = conn.createStatement()) {
			System.out.println("Connection is " + conn);
			String[] tableNames = new String[] {"COALCO", "COALTOWN"};
			for (String str: tableNames) {
				try (ResultSet rs = conn.getMetaData().getTables(null, null, str, null)) {
					if (rs.next()) {
						System.out.println("Droping existing Table " + str);
						String sqlDrop = "DROP TABLE " + str;
						stmt.executeUpdate(sqlDrop);
					}
				}  // end try resultSet	
			} // end for all tables
			stmt.executeUpdate("CREATE TABLE COALCO ("
			+ "id INTEGER PRIMARY KEY, "
			+ "name VARCHAR(255), "
			+ "date_founded DATE)");
			System.out.println("Created Company Table");
			stmt.executeUpdate("CREATE TABLE COALTOWN ("
			+ "id INTEGER PRIMARY KEY, "
			+ "name VARCHAR(255), "
			+ "owner_id integer, "
			+ "housing_units integer,"
			+ "date_built DATE)");
			System.out.println("Created Town Table");
			stmt.executeUpdate("INSERT INTO COALCO VALUES (1, 'Cambria Steel', '1880-01-01')");
			stmt.executeUpdate("INSERT INTO COALCO VALUES (2, 'Edwards Coal', '1921-06-01')");
			stmt.executeUpdate("INSERT INTO COALCO VALUES (3, 'Jamison Coal and Coke', '1890-09-01')");
			stmt.executeUpdate("INSERT INTO COALCO VALUES (4, 'Rochester and Pittsburgh', '1885-01-01')");
			stmt.executeUpdate("INSERT INTO COALCO VALUES (5, 'Sarah Coal',               '1905-01-01')");
			System.out.println("Created Company Entries");
			stmt.executeUpdate("INSERT INTO COALTOWN VALUES (1, 'Slickville',    1, 125, '1917-01-01')");
			stmt.executeUpdate("INSERT INTO COALTOWN VALUES (2, 'Ellesworth',    1, 120, '1914-01-01')");
			stmt.executeUpdate("INSERT INTO COALTOWN VALUES (3, 'Wherum',        1, 115, '1911-01-01')");
			stmt.executeUpdate("INSERT INTO COALTOWN VALUES (4, 'Edwards No. 2', 2, 6, '1920-01-01')");
			stmt.executeUpdate("INSERT INTO COALTOWN VALUES (5, 'Highland',      3, 55, '1890-01-01')");
			stmt.executeUpdate("INSERT INTO COALTOWN VALUES (6, 'Luxor',         3, 125, '1888-01-01')");
			stmt.executeUpdate("INSERT INTO COALTOWN VALUES (7, 'Forbes Road',   3, 125, '1889-01-01')");
			stmt.executeUpdate("INSERT INTO COALTOWN VALUES (8, 'Iselin',        4, 230, '1905-01-01')");
			stmt.executeUpdate("INSERT INTO COALTOWN VALUES (9, 'Whiskey Run',   4,  60, '1906-01-01')");
			stmt.executeUpdate("INSERT INTO COALTOWN VALUES (10, 'Hart Town',    4,  40, '1907-01-01')");
			stmt.executeUpdate("INSERT INTO COALTOWN VALUES (11, 'Nesbitt Run',  4,  24, '1908-01-01')");
			System.out.println("Created Coal Town Entries");
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS rowCount FROM COALCO");
			rs.next();
			int coalCoCount = rs.getInt("rowCount");
			rs.close();
			rs = stmt.executeQuery("SELECT COUNT(*) AS rowCount FROM COALTOWN");
			rs.next();
			int coalTownCount = rs.getInt("rowCount");
			rs.close();
			System.out.println("There are " + coalCoCount + " coal companies and " + coalTownCount + " coal towns.");
			
		} catch (Exception e) {
					System.out.println("Exception caught inside try Conn");
					e.printStackTrace();
			}
	}

}
