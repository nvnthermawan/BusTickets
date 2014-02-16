package com.jclc.busclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ViewTicketsActivity extends Activity {

	ArrayList<String> tickets = new ArrayList<String>();
	
	ArrayList<String> uts = new ArrayList<String>();
	
	ArrayList<String> dates = new ArrayList<String>();

	ListView listView;
	Context c;
	
	int id_user = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_tickets);
		
		Bundle b = getIntent().getExtras();
		
		if(b.containsKey("Iduser")){
			id_user = b.getInt("Iduser");
			Log.d("content","ON VIEW TICKETS: "+ id_user);
		}
		
		c = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_tickets, menu);
		return true;
	}
	
	public void refresh(View v){
		
		ViewUserTickets viewuser = new ViewUserTickets();
		viewuser.execute();	
	}
	
	public void showQRCode(int pos){
		Intent intent = new Intent(this,QRCodeActivity.class);
		intent.putExtra("UserTicket", uts.get(pos));
		intent.putExtra("Date", dates.get(pos));
		startActivity(intent);
	}
	
	
	public String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} 
			finally {
				is.close();
			}
			return writer.toString();
		} 

		else {       
			return "";
		}
	}
	
	public class ViewUserTickets extends AsyncTask<Object, Object, Object>{

		String sresponse = "error";
		
		@Override
		protected Object doInBackground(Object... params) {
			postData();
			return null;
		}
		
		protected void onPostExecute(Object result)
		{
			Model.LoadModel("ic_click_me.png", tickets);
			
			listView = (ListView) findViewById(R.id.listView);
	        String[] ids = new String[Model.Items.size()];
	        for (int i= 0; i < ids.length; i++){

	            ids[i] = Integer.toString(i+1);
	        }

	        ItemAdapter adapter = new ItemAdapter(c,R.layout.row, ids);
	        listView.setAdapter(adapter);
			
	        
	        listView.setOnItemClickListener(new OnItemClickListener() {
	            public void onItemClick(AdapterView<?> parent, View view,
	                    int position, long id) {
	                
	            	showQRCode(position);
	            	
	            }
	        });
			
			//ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewTicketsActivity.this,android.R.layout.simple_list_item_1, android.R.id.text1, tickets);        	
			//ListView lv = (ListView)findViewById(R.id.listView1);
			//lv.setAdapter(adapter);
		}

		public void postData() {
		    // Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost("http://192.168.102.50:1122/viewTickets");
		    
		    tickets.clear();
		    uts.clear();
		    dates.clear();
		    

		    try {
		        // Add your data
		    	JSONObject holder = new JSONObject();

		    	try {
		    		holder.put("id_user", id_user);
				} catch (JSONException e) {
					Log.d("content","JSON CREATION FOR POST: " + e.getMessage());
				}
		    	
		    	Log.d("content","HOLDER: " + holder.toString());
		    	
		    	StringEntity se = new StringEntity(holder.toString());
		    	
		        httppost.setEntity(se);
		        
		        httppost.setHeader("Accept", "application/json");
		        httppost.setHeader("Content-type", "application/json");

		        // Execute HTTP Post Request
		        HttpResponse response = httpclient.execute(httppost);
		        
		        InputStream in = response.getEntity().getContent();
				sresponse = convertStreamToString(in);
				Log.d("content","SRESPONSE: " + sresponse);
				
				try {

					JSONObject holder2 = new JSONObject(sresponse.trim());
					
					if(holder2.has("list")){
						JSONArray list = new JSONArray(holder2.getString("list"));

						for (int i=0; i<list.length(); i++){
							try{
								JSONObject value = list.getJSONObject(i);

								String type = "error";
								
								if(value.has("type")){
									if(value.getInt("type") == 1)
										type = "15 minutes";
									if(value.getInt("type") == 2)
										type = "30 minutes";
									if(value.getInt("type") == 3)
										type = "60 minutes";
								}
								tickets.add("Ticket: " + value.getInt("id_ticket") + "\n" 
										+ "Limit: " + type);
								uts.add(value.getInt("id_ticket") + "," + id_user);
								if(value.has("date")){
									dates.add("Expires at: " + value.getString("date"));
								}
								else dates.add("Ticket was not used yet!");
								
								//Log.d("content", "OLAAAAAAAA" + "Ticket " + value.getInt("id_ticket") + "\n" 
								//	+ "Type: " + value.getInt("type"));
							}catch(Exception e){
								Log.d("content", "Error on JSON 2: " + e.getMessage());
							}

						}
					}
					else {
						Toast.makeText(getApplicationContext(), "No tickets to show!", Toast.LENGTH_LONG).show();
					}
				} catch (JSONException e) {
					Log.d("content", "Error on JSON: " + e.getMessage());
				}
				
		    } catch (ClientProtocolException e) {
		        Log.d("content","CLIENTPROTOCOL: " + e.getMessage());
		    } catch (IOException e) {
		    	Log.d("content","IOEXCEPTION: " + e.getMessage());
		    }
		}

	}


}
