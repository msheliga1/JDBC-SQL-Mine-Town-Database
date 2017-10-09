package mineDatabase;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JButton;
import javax.swing.JEditorPane;  // for editing text
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class MineDBManager implements ActionListener {
	
	private Statement stmt;  // used for action Listeners
	
	public static final String ALL_TOWNS = "Display All Towns";
	public static final String ALL_COMPANIES = "Display All Companies";
	public static final String RECORD_COUNTS = "Count of Records";
	public static final String TOWNS_JOINED_COMPANIES = "Towns with Company Name";
	public static final String TOWNS_OUTTER_COMPANIES = "Towns with All Companies";
	public static final String TOWNS_SIZE_SORT = "Towns Sorted by Size";
	public static final String HOUSE_COUNT = "Total Houses per Company";
	public static final String NAME_WITH_COAL = "Company Names with Coal";
	public static final String DATE_ERROR = "Towns Built Before Company Founded (Error)";
	
	// sample main routine simply calls the mine manager to setup the DB and GUI, then waits for user input.
	public static void main(String args[]) {
		new MineDBManager();			
	} // end main
	
    // Open the database securing a statement.  Then set up the mine manager GUI.  Finally wait for user input.
	public MineDBManager( ) {
		System.out.println("Running Mining Town Database Manager");
		String url = "jdbc:derby:coalCamps;create=true";
		try {  // try with resources will shut connection b4 buttons are clicked!
			Connection conn = DriverManager.getConnection(url);
			stmt = conn.createStatement();
			System.out.println("Connection is " + conn);
			String[] tableNames = new String[] {"COALCO", "COALTOWN"};

            // Now setup the GUI
			JFrame myFrame = new JFrame();
			// JEditorPane myPane = new JEditorPane("text/html", sb.toString());
			JPanel myPanel = new JPanel( );
			// Add buttons
			JButton companyButton = new JButton(ALL_COMPANIES);
			companyButton.setPreferredSize(new Dimension(200, 20));
			myPanel.add(companyButton);
			companyButton.addActionListener(this);
			JButton townButton = new JButton(ALL_TOWNS);
			townButton.setPreferredSize(new Dimension(200, 20));
			myPanel.add(townButton);
			townButton.addActionListener(this);
			JButton countButton = new JButton(RECORD_COUNTS);
			countButton.setPreferredSize(new Dimension(200, 20));
			myPanel.add(countButton);
			countButton.addActionListener(this);

            // Inner, Outter joins and sort by size
			JButton townCompanyButton = new JButton(TOWNS_JOINED_COMPANIES);
			townCompanyButton.setPreferredSize(new Dimension(200, 20));
			myPanel.add(townCompanyButton);
			townCompanyButton.addActionListener(this);
			JButton outterCompanyButton = new JButton(TOWNS_OUTTER_COMPANIES);
			outterCompanyButton.setPreferredSize(new Dimension(200, 20));
			myPanel.add(outterCompanyButton);
			outterCompanyButton.addActionListener(this);
			JButton townSizeButton = new JButton(TOWNS_SIZE_SORT);
			townSizeButton.setPreferredSize(new Dimension(200, 20));
			myPanel.add(townSizeButton);
			townSizeButton.addActionListener(this);
			
            // House Count per Company, Name Includes Coal, Date Error
			JButton houseCountButton = new JButton(HOUSE_COUNT);
			houseCountButton.setPreferredSize(new Dimension(200, 20));
			myPanel.add(houseCountButton);
			houseCountButton.addActionListener(this);
			JButton nameCoalButton = new JButton(NAME_WITH_COAL);
			nameCoalButton.setPreferredSize(new Dimension(200, 20));
			myPanel.add(nameCoalButton);
			nameCoalButton.addActionListener(this);
			JButton dateErrorButton = new JButton(DATE_ERROR);
			dateErrorButton.setPreferredSize(new Dimension(400, 20));
			myPanel.add(dateErrorButton);
			dateErrorButton.addActionListener(this);
			
			// Now setup the frame(window) and panel
			myFrame.add(myPanel);
			myFrame.setVisible(true);
			myFrame.setSize(500, 500);
			myFrame.setTitle("Coal Town Database Manager");
			myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
		} catch (Exception e) {
			System.out.println("Exception caught inside mineDBManager.");
			e.printStackTrace();
		}  // end try-catch
	} // end  mineDBManager
	
    // Respond to all button presses by calling routines to perform the indicated SQL Query
	public void actionPerformed(ActionEvent e) {
		// System.out.println("Button Clicked");
	    Object obj = e.getSource();
	    if (obj instanceof JButton) {
	      JButton button = (JButton) obj;
	      // System.out.println("The buttons label is " + button.getText());  // getLabel deprecated
	      if (button.getText().equals(ALL_TOWNS)) {
	    	displayTownTable();
	      } else if (button.getText().equals(ALL_COMPANIES)) {
		    displayCompanyTable( );
	      } else if (button.getText().equals(RECORD_COUNTS)) {
	    	displayRecordCounts( );  // display number of records in each table
	      } else if (button.getText().equals(TOWNS_JOINED_COMPANIES)) {
	    	displayTownsJoinedCompanies( );  // display towns with correct company name using an inner join
	      } else if (button.getText().equals(TOWNS_OUTTER_COMPANIES)) {
	    	displayTownsOuterCompanies( );  // display towns and all companies (outer join)
	      } else if (button.getText().equals(TOWNS_SIZE_SORT)) {
	    	displayTownsSortedSize( );  // display towns sorted by number of houses
	      } else if (button.getText().equals(HOUSE_COUNT)) {
	    	displayHouseCountByCompany( );  // display total houses per company
	      } else if (button.getText().equals(NAME_WITH_COAL)) {
	    	displayTownsNamedCoal( );  // display towns where company contains the word Coal
	      } else if (button.getText().equals(DATE_ERROR)) {
	    	displayTownDateError( );  // display towns founded before company (which is an error).
	      } else {
			System.out.println("Unrecognized Button Text of " + button.getText());	    	 
	      } // end if-elseif 
	    } else {
	      System.out.println("GameButtons ActionPerformed not from JButton - was a " + obj);
	    }
	} // end actionPerformed
	
    // Display a result set in HTML format.
	public void displayTableAsHTML(ResultSet rs, String header) throws SQLException {
			ResultSetMetaData rsmd = rs.getMetaData();
			
			StringBuilder sb = new StringBuilder("<HTML> <HEAD> <TITLE> " + header + " </TITLE> </HEAD>  <TABLE border='2'> ");
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				sb.append("<TH> " + rsmd.getColumnLabel(i) + " </TH>");
			}
			while (rs.next()) {
				sb.append( "<TR>");
				for (int i = 1; i <= rsmd.getColumnCount(); i++) {
					sb.append("<TD> ");
					if (rs.getString(i) != null) sb.append(rs.getString(i));
					sb.append(" </TD>");
				}
				sb.append(" </TR> ");
			} // end while
			rs.close();
			sb = sb.append(" </TABLE> </HTML>");
			JOptionPane.showMessageDialog(null, sb.toString(), header, JOptionPane.INFORMATION_MESSAGE);
	} // end  displayTableAsHTML
	
    // display the number of records in all tables.
	public void displayRecordCounts() {
		try {
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS rowCount FROM COALCO");
			rs.next();
			int coalCoCount = rs.getInt("rowCount");
			rs.close();
			rs = stmt.executeQuery("SELECT COUNT(*) AS rowCount FROM COALTOWN");
			rs.next();
			int coalTownCount = rs.getInt("rowCount");
			rs.close();
			StringBuilder sb = new StringBuilder("There are " + coalCoCount + " coal companies and " + coalTownCount + " coal towns.");
			JOptionPane.showMessageDialog(null, sb.toString(), "Count of Records in Each Table", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			System.out.println("Exception caught inside displayRecordCounts");
			e.printStackTrace();
		}  // end try-catch
	} // end  displayRecordCounts
	
    // Display the table of coal companies using raw data in HTML format.
	public void displayCompanyTable() {
		try {
			ResultSet rs = stmt.executeQuery("SELECT id as Number, name as Company_Name, date_founded FROM COALCO");
			displayTableAsHTML(rs, "All Coal Companies");
		} catch (Exception e) {
			System.out.println("Exception caught inside displayCompanyTable");
			e.printStackTrace();
		}  // end try-catch
	} // end  displayCompanyTable
	
    // Display the table of coal towns using raw data in HTML format. 
	public void displayTownTable() {
		try {
			ResultSet rs = stmt.executeQuery("SELECT * FROM COALTOWN");
			displayTableAsHTML(rs, "All Coal Towns");
		} catch (Exception e) {
			System.out.println("Exception caught inside displayTownTable");
			e.printStackTrace();
		}  // end try-catch
	} // end  displayTownTable
	
	// Display a user friendly table of coal towns with company index replaced by name (inner join) in HTML format. 
	public void displayTownsJoinedCompanies() {
		try {
			StringBuilder SQL = new StringBuilder("SELECT COALTOWN.name AS town, COALCO.name AS Company, COALTOWN.housing_units, COALTOWN.date_built");
			SQL.append(" FROM COALTOWN INNER JOIN COALCO ON COALTOWN.owner_id = COALCO.id");
			ResultSet rs = stmt.executeQuery(SQL.toString());
			displayTableAsHTML(rs, "All Coal Towns with Company Name (Inner Join Example)");
		} catch (Exception e) {
			System.out.println("Exception caught inside displayTownsJoinedCompanies");
			e.printStackTrace();
		}  // end try-catch
	} // end  displayTownsJoinedCompanies 
	
	// Display in HTML format a user friendly table of coal towns with all companies displayed (outter join), even if the company 
	// does not own any company town. 
	public void displayTownsOuterCompanies() {
		try {
			StringBuilder SQL = new StringBuilder("SELECT COALTOWN.name AS town, COALCO.name AS Company, COALTOWN.housing_units, COALTOWN.date_built");
			SQL.append(" FROM COALTOWN RIGHT OUTER JOIN COALCO ON COALTOWN.owner_id = COALCO.id");
			ResultSet rs = stmt.executeQuery(SQL.toString());
			displayTableAsHTML(rs, "All Coal Towns with all Companies (Outer Join Example)");
		} catch (Exception e) {
			System.out.println("Exception caught inside displayTownsOuterCompanies");
			e.printStackTrace();
		}  // end try-catch
	} // end  displayTownsOuterCompanies
	
	// Display in HTML format a user friendly table of coal towns sorted by size. 
	public void displayTownsSortedSize() {
		try {
			StringBuilder SQL = new StringBuilder("SELECT COALTOWN.name AS town, COALCO.name AS Company, COALTOWN.housing_units, COALTOWN.date_built");
			SQL.append(" FROM COALTOWN INNER JOIN COALCO ON COALTOWN.owner_id = COALCO.id");
			SQL.append(" ORDER BY COALTOWN.housing_units");
			ResultSet rs = stmt.executeQuery(SQL.toString());
			displayTableAsHTML(rs, "All Coal Towns Sorted by Size");
		} catch (Exception e) {
			System.out.println("Exception caught inside displayTownsSortedSize");
			e.printStackTrace();
		}  // end try-catch
	} // end  displayTownsSortedSize	
	
	// Display a user friendly table of coal towns with company index replaced by name (inner join) in HTML format. 
	public void displayHouseCountByCompany( ) {
		try {
			StringBuilder SQL = new StringBuilder("SELECT derived.name,  SUM(derived.housing_units) AS house_sum");
			SQL.append(" FROM (SELECT COALTOWN.owner_id, COALTOWN.housing_units, COALCO.name ");
			SQL.append(" FROM COALTOWN inner join COALCO ON COALTOWN.owner_id = COALCO.id ) AS derived ");
			SQL.append(" GROUP BY derived.name");
			ResultSet rs = stmt.executeQuery(SQL.toString());
			displayTableAsHTML(rs, "Total Houses ");
		} catch (Exception e) { 
			System.out.println("Exception caught inside displayHouseCount");
			e.printStackTrace();
		}  // end try-catch
	} // end  displayHouseCountByCompany 
	
	// Display in HTML format a user friendly table of coal towns whose company contains the word coal. 
	public void displayTownsNamedCoal() {
		try {
			StringBuilder SQL = new StringBuilder("SELECT COALTOWN.name AS town, COALCO.name AS Company, COALTOWN.housing_units, COALTOWN.date_built");
			SQL.append(" FROM COALTOWN INNER JOIN COALCO ON COALTOWN.owner_id = COALCO.id");
			SQL.append(" WHERE COALCO.name LIKE '%Coal%' ");
			ResultSet rs = stmt.executeQuery(SQL.toString());
			displayTableAsHTML(rs, "All Coal Towns with Coal in Company Name");
		} catch (Exception e) {
			System.out.println("Exception caught inside displayTownsNamedCoal");
			e.printStackTrace();
		}  // end try-catch
	} // end  displayTownsNamedCoal 
	
	
	// Display in HTML format a user friendly table of coal towns whose company contains the word coal. 
	public void displayTownDateError() {
		try {
			StringBuilder SQL = new StringBuilder("SELECT COALTOWN.name AS town, COALTOWN.date_built, COALCO.name AS Company, COALCO.date_founded");
			SQL.append(" FROM COALTOWN INNER JOIN COALCO ON COALTOWN.owner_id = COALCO.id");
			SQL.append(" WHERE COALTOWN.date_built < COALCO.date_founded" );
			ResultSet rs = stmt.executeQuery(SQL.toString());
			displayTableAsHTML(rs, "All Coal Towns Built Before Company was Founded (Errors)");
		} catch (Exception e) {
			System.out.println("Exception caught inside displayTownDateError");
			e.printStackTrace();
		}  // end try-catch
	} // end  displayTownDateError 
}  // end class MineDBManager
