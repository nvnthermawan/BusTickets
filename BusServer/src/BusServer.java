import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import static spark.Spark.*;
import spark.*;




public class BusServer {

	static Map<String,User> users = new HashMap<String,User>();

	static TicketLine tline = new TicketLine();

	public static void main(String[] args) {
		addUser("luis", "12345", "Luis Costa", "1767564354", "13-1-2017", "VISA");
		addUser("joao", "12345", "Joao Campos", "154676435", "13-1-2017", "VISA");

		//TicketLine tline1 = new TicketLine();
		//tline.buyTickets(1, 0, 1);
		//tline.validateTicket(4, 0, 0);
		//System.out.println("TICKETS: " + tline.viewTickets(0));

		//System.out.println("TICKETS: " + tline.viewBusTickets(0));

		/*JSONObject js = new JSONObject();
		try {
			js.put("response", "NOK");
			js.put("t1_space", 1);
			js.put("t2_space", 2);
			js.put("t3_space", 3);
			System.out.println(js.toString());
		} catch (JSONException e) {
			System.out.println("Error: Something went wrong in JSON creation!");
		}*/

		setPort(1122);

		//--------------------------------LOGIN------------------------------
		//System.out.println(validateLogin("luis", "12345")); // username, password
		post(new spark.Route("/login") { // JSON A ENVIAR: {"username":"user", "password":"pass"} 
			// RESPOSTA JSON: {"id_user":id_user}
			@Override
			public Object handle(Request request, Response response) {


				String username = "";
				String password = "";

				try {
					System.out.println(request.body());
					JSONObject json = new JSONObject(request.body());
					//JSONObject jstart = json.getJSONObject("login");
					if(json.has("username"))
						username = json.getString("username");	
					if(json.has("password"))
						password = json.getString("password");

					System.out.println("u,p: " + username + " " + password);

				} catch (JSONException e1) {
					return "Error: Something went wrong in JSON creation!";
				}

				if(username.equals("") || password.equals(""))
					return "{\"id_user\":-1}";
				else
					return validateLogin(username, password);

			}
		});


		//---------------------------------REGISTER-----------------------------------------
		post(new spark.Route("/register") { // JSON A ENVIAR: {"username":"user", 
			//				"password":"pass", "name":"name", 
			//	            "cc_number"="number", "cc_date"="date", "cc_type"="type"}
			// RESPOSTA JSON: {"response":"OK"} NOK
			@Override
			public Object handle(Request request, Response response) {

				String username = "";
				String password = "";
				String name = "";
				String cc_number = "";
				String cc_date = "";
				String cc_type = "";

				try {
					System.out.println(request.body());
					JSONObject json = new JSONObject(request.body());
					//JSONObject jstart = json.getJSONObject("login");

					if(json.has("username"))
						username = json.getString("username");	
					if(json.has("password"))
						password = json.getString("password");
					if(json.has("name"))
						name = json.getString("name");
					if(json.has("cc_number"))
						cc_number = json.getString("cc_number");
					if(json.has("cc_date"))
						cc_date = json.getString("cc_date");	
					if(json.has("cc_type"))
						cc_type = json.getString("cc_type");

					System.out.println("u, p: " + username + " " + password);
					System.out.println("n: " + name);
					System.out.println("num, date, type: " + cc_number + " " + cc_date + " " + cc_type);

				} catch (JSONException e1) {
					return "Error: Something went wrong in JSON creation!";
				}
				
				if(users.containsKey(username)){
					username = "";
				}
				
				JSONObject js = new JSONObject();
				try {

					if(username.equals("") || password.equals("") || name.equals("") 
							|| cc_number.equals("") 
							|| cc_date.equals("") || cc_type.equals("")){
						js.put("response", "NOK");
						if(username.equals(""))
							js.put("username", -1);
						else js.put("username", 1);
						if(password.equals(""))
							js.put("password", -1);
						else js.put("password", 1);
						if(name.equals(""))
							js.put("name", -1);
						else js.put("name", 1);
						if(cc_number.equals(""))
							js.put("cc_number", -1);
						else js.put("cc_number", 1);
						if(cc_date.equals(""))
							js.put("cc_date", -1);
						else js.put("cc_date", 1);
						if(cc_type.equals(""))
							js.put("cc_type", -1);
						else js.put("cc_type", 1);

					}
					else{
						addUser(username, password, name, cc_number, cc_date, cc_type);
						js.put("response", "OK");
					}

					return js.toString();

				} catch (JSONException e) {
					return "Error: Something went wrong in JSON creation!";
				}





			}
		});

		//-----------------------------------------BUY TICKETS----------------------------
		//System.out.println(tline.howManyTicketsCanBuy(0,1)); // id_user, ticket_type - how many tickets from a type a buyer can bought
		//tline.buyTickets(1, 0, 1); // quantity, id_user, type
		post(new spark.Route("/buyTickets") { // JSON A ENVIAR: {"id_user":id_user, "t1":quantity_t1, "t2":quantity_t2, "t3":quantity_t3} 
			// RESPOSTA JSON: {"response":"OK"}
			// RESPOSTA JSON2: {"response":"NOK", "t1_curr":curr_space1, "t2_curr":curr_space2, "t3_curr":curr_space3}
			@Override
			public Object handle(Request request, Response response) {

				int t1 = -1;
				int t2 = -1;
				int t3 = -1;
				int id_user = -1;

				try {
					JSONObject json = new JSONObject(request.body());
					t1 = json.getInt("t1");
					t2 = json.getInt("t2");
					t3 = json.getInt("t3");
					id_user = json.getInt("id_user");

					System.out.println("buytickets: " + t1 + " " + t2 + " " + t3);

				} catch (JSONException e1) {
					return "Error: Something went wrong in JSON creation!";
				}

				int t1_curr = tline.howManyTicketsCanBuy(id_user,1);
				int t2_curr = tline.howManyTicketsCanBuy(id_user,2);
				int t3_curr = tline.howManyTicketsCanBuy(id_user,3);

				boolean f1=false;
				boolean f2=false;
				boolean f3=false;

				if(t1_curr >= t1){
					f1 = true;
				}

				if(t2_curr >= t2){
					f2 = true;
				}

				if(t3_curr >= t3){
					f3 = true;
				}

				if(f1 && f2 && f3){
					tline.buyTickets(t1, id_user, 1);
					tline.buyTickets(t2, id_user, 2);
					tline.buyTickets(t3, id_user, 3);
					if(t1+t2+t3 >= 10 && t1==0 && t2==0)
						tline.buyTickets(t3, id_user, 1);
					else if(t1+t2+t3 >= 10 && t1==0 && t2>0)
						tline.buyTickets(t2, id_user, 1);
					else if(t1+t2+t3 >= 10 && t1>0)
						tline.buyTickets(t1, id_user, 1);

					return "{\"response\":\"OK\"}";
				}

				JSONObject js = new JSONObject();
				try {
					js.put("response", "NOK");
					js.put("t1_space", t1_curr);
					js.put("t2_space", t2_curr);
					js.put("t3_space", t3_curr);

				} catch (JSONException e) {
					return "Error: Something went wrong in JSON creation!";
				}

				return js.toString();
			}
		});

		//---------------------------------------VALIDATE TICKET-------------------------
		//System.out.println(tline.validateTicket(2, 0, 0)); // id_ticket, id_user, id_validator
		//System.out.println(tline.addTimeType(-1)); // current time plus type of ticket, type 4 for the last 90 minutes
		post(new spark.Route("/validateTicket") { // JSON A ENVIAR: {"id_ticket":id, "id_user":id, "id_validator":id} 
			// RESPOSTA JSON: {"response":"OK"} "NOK"
			@Override
			public Object handle(Request request, Response response) {

				int id_ticket = -1;
				int id_user = -1;
				int id_validator = -1;

				try {
					System.out.println(request.body());
					JSONObject json = new JSONObject(request.body());
					//JSONObject jstart = json.getJSONObject("login");
					if(json.has("id_ticket"))
						id_ticket = json.getInt("id_ticket");	
					if(json.has("id_user"))
						id_user = json.getInt("id_user");
					if(json.has("id_validator"))
						id_validator = json.getInt("id_validator");

					System.out.println("t,u,v: " + id_ticket + " " + id_user + " " + id_validator);

				} catch (JSONException e1) {
					return "Error: Something went wrong in JSON creation!";
				}

				if(id_ticket == -1 || id_user == -1 || id_validator == -1)
					return "{\"response\":\"NOK\"}";
				else {
					int isvalid = tline.validateTicket(id_ticket, id_user, id_validator);
					String enddate = tline.enddate(id_ticket);
					if(id_ticket == isvalid)
						return "{\"response\":\"OK\",\"date\":\""+ enddate +"\"}";
					else
						return "{\"response\":\"NOK\"}";


				}

			}
		});

		//---------------------------------------------VIEW TICKETS---------------------------
		//tline.viewTickets(1); // id_user
		post(new spark.Route("/viewTickets") { // JSON A ENVIAR: {"id_user":id} 
			// RESPOSTA JSON: {"id_ticket":{"id_ticket":id, "type":"type"} "NOK"
			@Override
			public Object handle(Request request, Response response) {

				int id_user = -1;

				try {
					System.out.println(request.body());
					JSONObject json = new JSONObject(request.body());	
					if(json.has("id_user"))
						id_user = json.getInt("id_user");

					System.out.println("u: " + id_user);

				} catch (JSONException e1) {
					return "Error: Something went wrong in JSON creation!";
				}

				if(id_user == -1)
					return "{\"response\":\"NOK\"}";
				else {
					return tline.viewTickets(id_user);
				}
			}
		});

		//----------------------------------------VIEW BUS TICKETS----------------------------
		//tline.viewBusTickets(id_validator)
		post(new spark.Route("/viewBusTickets") { // JSON A ENVIAR: {"id_validator":id} 
			// RESPOSTA JSON: {"id_ticket":{"id_ticket":id, "type":"type"} "NOK"
			@Override
			public Object handle(Request request, Response response) {

				int id_validator = -1;

				try {
					System.out.println(request.body());
					JSONObject json = new JSONObject(request.body());	
					if(json.has("id_validator"))
						id_validator = json.getInt("id_validator");

					System.out.println("val: " + id_validator);

				} catch (JSONException e1) {
					return "Error: Something went wrong in JSON creation!";
				}

				if(id_validator == -1)
					return "{\"response\":\"NOK\"}";
				else {
					return tline.viewBusTickets(id_validator);
				}
			}
		});

		//---------------------------------------IS TICKET VALID?-------------------------
		//tline.isTicketValid(18); // is ticket with id 'X' valid?
		post(new spark.Route("/isTicketValid") { // JSON A ENVIAR: {"id_ticket":id} 
												  // RESPOSTA JSON: {"response":"OK"} "NOK"
			@Override
			public Object handle(Request request, Response response) {

				int id_ticket = -1;
				
				try {
					System.out.println(request.body());
					JSONObject json = new JSONObject(request.body());
					//JSONObject jstart = json.getJSONObject("login");
					if(json.has("id_ticket"))
						id_ticket = json.getInt("id_ticket");	
					
					System.out.println("t: " + id_ticket);

				} catch (JSONException e1) {
					return "Error: Something went wrong in JSON creation!";
				}

				if(id_ticket == -1)
					return "{\"response\":\"NOK\"}";
				else {
					int isvalid = tline.isTicketValid(id_ticket);
					String enddate = tline.enddate(id_ticket);
					if(isvalid != -1)
						return "{\"response\":\"OK\",\"date\":\""+ enddate +"\"}";
					else
						return "{\"response\":\"NOK\",\"date\":\""+ enddate +"\"}";


				}

			}
		});
		


	} 

	public static void addUser(String u, String p, String n, String cn, String cv, String ct){
		User nuser = new User(users.size(), u, p, n, cn, cv, ct);
		users.put(u,nuser);
	}

	public static String validateLogin(String username, String password){

		if(users.containsKey(username)){
			return "{\"id_user\":" + users.get(username).login(username, password) + "}"; // returns id
		}

		return "{\"id_user\":-1}";
	}
}
