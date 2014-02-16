import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class TicketLine {
	String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	String username = "lc";
	String password = "lc";
	String dbname = "jdbc:mysql://192.168.102.50:3306/bilheteira";
	String tableName = "bilhete";
	Connection con = null;
	Statement statement= null;

	boolean flag = true;

	TicketLine(){}

	public void connectToDB(){
		try {
			Class.forName(JDBC_DRIVER);
			con = DriverManager.getConnection(dbname,username,password);

			statement= con.createStatement();
			System.out.println ("Database connection established");

			flag = true;
		} catch (SQLException e1) {
			System.out.println ("Error on SQL. Can't connect to DB: " + e1.getMessage());
			flag = false;
		} catch (ClassNotFoundException e) {
			System.out.println("Error on class. Can't connect to DB: " + e.getMessage());
			flag = false;
		}
	}
	
	public String enddate(int id_ticket){
		String result = "";
		connectToDB();
		
		if(flag){
			String sql = "SELECT validtill FROM bilhete WHERE id=" + id_ticket + "";

			System.out.println("Reading from database...");

			try {

				ResultSet rs = statement.executeQuery(sql);
				System.out.println("Finished db reading!");

				if (rs.next()) {
					result = rs.getString("validtill");
				}

			} catch (SQLException e) {
				System.err.println("Error reading DB: " + e.getMessage());
			}

			closeConnToDB();		
		}		
		return result;
	}
	public void closeConnToDB(){
		try {
			con.close();
			System.out.println("Connection to DB closed.");
		} catch (SQLException e) {
			System.err.println("Error closing connection: " + e.getMessage());
		}
	}

	public String addTimeType(int type){

		Calendar now = Calendar.getInstance();
		if(type == 1){
			now.add(Calendar.MINUTE, 15+10); //10m + 10s is the difference in time between the server's time and the real time.
			now.add(Calendar.SECOND, 10);
		}if(type == 2){
			now.add(Calendar.MINUTE, 30+10);
			now.add(Calendar.SECOND, 10);
		}if(type == 3){
			now.add(Calendar.MINUTE, 60+10);
			now.add(Calendar.SECOND, 10);
		}if(type == 4){
			now.add(Calendar.MINUTE, -90-10);
			now.add(Calendar.SECOND, -10);
		}
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return dateFormat.format(now.getTime()).toString();
	}

	public int validateTicket(int id_ticket, int id_user, int id_validator){

		int result = -1;

		connectToDB();

		if(flag){
			String sql = "SELECT validtill,type FROM bilhete WHERE id_user=" + id_user + " AND id=" + id_ticket + "";

			System.out.println("Reading from database...");

			String validtill = "";

			int type_ticket = -1;

			try {

				ResultSet rs = statement.executeQuery(sql);
				System.out.println("Finished db reading!");

				if (rs.next()) {
					validtill = rs.getString("validtill");
					type_ticket = rs.getInt("type");
				}

				if (validtill == null){
					String sql2 = "UPDATE bilhete SET validtill='" + addTimeType(type_ticket) + "', id_validator=" + id_validator 
							+ " WHERE id_user=" + id_user + " AND id=" + id_ticket + "";

					statement.executeUpdate(sql2);

					result = id_ticket;
				}

			} catch (SQLException e) {
				System.err.println("Error reading DB: " + e.getMessage());
			}

			closeConnToDB();
		}

		return result;
	}


	public String viewTickets(int id_user){
		connectToDB();
		
		if(flag){
			JSONObject js = new JSONObject();

			String last90m = addTimeType(4);

			String sql = "SELECT id,type,validtill FROM bilhete WHERE id_user=" 
					+ id_user + " AND ((validtill is NULL) OR validtill >= '" + last90m + "')";
			
			System.out.println("Reading from database...");

			try {

				ResultSet rs = statement.executeQuery(sql);
				System.out.println("Finished db reading!");

				JSONArray list = new JSONArray();
				while (rs.next()) {
					try {
						JSONObject js1 = new JSONObject();
						js1.put("id_ticket",rs.getInt("id"));
						js1.put("type",rs.getInt("type"));
						js1.put("date", rs.getString("validtill"));
						list.put(js1);
					} catch (JSONException e) {
						System.out.println("Error: Something went wrong in JSON creation!");
					} catch (Exception e) {
						System.out.println("Error: " + rs.getInt("id") + " " + e.getMessage());
					} 
					
				}
				try {
					js.put("list", list);
					System.out.println("OLA " + js);
				} catch (JSONException e) {
					System.out.println("Error: Something went wrong in JSON creation!");
				}
				
			} catch (SQLException e) {
				System.err.println("Error reading DB: " + e.getMessage());
			}

			closeConnToDB();

			return js.toString();
		}
		else return "{}";
	}

	public void buyTickets(int quantity, int id_user, int type){

		connectToDB();

		if(flag){

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = new Date();

			String currdate = dateFormat.format(date).toString();

			//INSERT INTO table_name (column1, column2, column3,...)
			//VALUES (value1, value2, value3,...)

			for(int i=0; i<quantity; i++){
				String sql="INSERT INTO bilhete" 
						+ "\n (" + "id_user, type, date" + ")" 
						+ "\n VALUES ('" + id_user + "'," 
						+ type + ",'" + currdate + "')";

				try {

					System.out.println("Writing into the database...");

					statement.executeUpdate(sql);

					System.out.println("Finished db writing!");
				}
				catch (SQLException e) {
					System.err.println("Error writing in DB: " + e.getMessage());
				}

			}

			closeConnToDB();
		}

	}

	public int howManyTicketsCanBuy(int id_user, int type){

		int counter = -1;

		connectToDB();

		if(flag){
			String sql = "SELECT count(*) AS counter FROM bilhete WHERE id_user=" 
					+ id_user + " AND type=" + type + " AND validtill is NULL";

			System.out.println("Reading from database...");



			try {

				ResultSet rs = statement.executeQuery(sql);
				System.out.println("Finished db reading!");

				if (rs.next()) {
					counter = 10 - rs.getInt("counter");
				}

				if (counter <= 0){
					counter = 0;
				}


			} catch (SQLException e) {
				System.err.println("Error reading DB: " + e.getMessage());
			}

			closeConnToDB();
		}
		return counter;
	}

	public String viewBusTickets(int id_validator){

		connectToDB();
		if(flag){
			JSONObject js = new JSONObject();

			String last90m = addTimeType(4);

			String sql = "SELECT id,type FROM bilhete WHERE id_validator=" + id_validator + " AND validtill >= '" + last90m + "'";


			System.out.println("Reading from database...");

			try {

				ResultSet rs = statement.executeQuery(sql);
				System.out.println("Finished db reading!");

				while (rs.next()) {
					JSONObject js1 = new JSONObject();
					try {
						js1.put("id_ticket",rs.getInt("id"));
						js1.put("type",rs.getInt("type"));
						System.out.println(js.toString());
						js.put(rs.getInt("id")+"", js1);
					} catch (JSONException e) {
						System.out.println("Error: Something went wrong in JSON creation!");
					}
				}

			} catch (SQLException e) {
				System.err.println("Error reading DB: " + e.getMessage());
			}

			closeConnToDB();

			return js.toString();
		}
		else return "{}";
	}

	public int isTicketValid(int id_ticket){

		int id = -1;

		connectToDB();

		if(flag){
			String currdate = addTimeType(-1);

			String sql = "SELECT id FROM bilhete WHERE id=" + id_ticket + 
					" AND validtill >= '" + currdate + "'";


			System.out.println(sql);

			System.out.println("Reading from database...");

			try {

				ResultSet rs = statement.executeQuery(sql);
				System.out.println("Finished db reading!");


				if (rs.next()) {
					id = rs.getInt("id");
				}


			} catch (SQLException e) {
				System.err.println("Error reading DB: " + e.getMessage());
			}

			closeConnToDB();
		}
		return id;
	}

}
