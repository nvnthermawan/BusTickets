package com.jclc.businspector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class TicketsActivity extends Activity {
	
	ArrayList<String> tickets = new ArrayList<String>();
	int thisticketsid = 0;

	int id_validator=0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tickets);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tickets, menu);
		return true;
	}
	
	public void refresh(View v){
		EditText editTextPlainTextInput = (EditText) this.findViewById(R.id.editText1);
		
		if(!TextUtils.isEmpty(editTextPlainTextInput.getText().toString())){
			id_validator = Integer.parseInt(editTextPlainTextInput.getText().toString());
			
			Toast.makeText( TicketsActivity.this, "Bus changed to: " 
					+ editTextPlainTextInput.getText().toString(), Toast.LENGTH_SHORT).show();
	
			
			ViewBusTickets viewbus = new ViewBusTickets();
			viewbus.execute();
		}
		else Toast.makeText( TicketsActivity.this, "BUS FIELD IS EMPTY!", Toast.LENGTH_LONG).show();
	}
	
	public class ViewBusTickets extends AsyncTask<Object, Object, Object>{

		String sresponse = "error";
		
		@Override
		protected Object doInBackground(Object... params) {
			postData();
			return null;
		}
		
		protected void onPostExecute(Object result)
		{
			//tickets.add("OLA\nOLE");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(TicketsActivity.this,android.R.layout.simple_list_item_1, android.R.id.text1, tickets);        	
			ListView lv = (ListView)findViewById(R.id.listView1);
			lv.setAdapter(adapter);
		}

		public void postData() {
		    // Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost("http://192.168.102.50:1122/viewBusTickets");

		    try {
		        // Add your data
		    	JSONObject holder = new JSONObject();

		    	try {
		    		holder.put("id_validator", id_validator);
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
				
				if(thisticketsid != id_validator){
					thisticketsid = id_validator;
					tickets.clear();
				}
				
				try {

					JSONObject holder2 = new JSONObject(sresponse.trim());
					
					Iterator<?> keys = holder2.keys();
				    while(keys.hasNext()){
				        String key = (String) keys.next();
				        try{
				             JSONObject value = holder2.getJSONObject(key);
				             
				             String type = "error";
				             if(value.getInt("type") == 1)
				            	 type = "15 minutes";
				             if(value.getInt("type") == 2)
				            	 type = "30 minutes";
				             if(value.getInt("type") == 3)
				            	 type = "60 minutes";
				             if(!tickets.contains("Ticket: " + value.getInt("id_ticket") + "\n" 
				            		 		+ "Limit: " + type)){
				            	 tickets.add("Ticket: " + value.getInt("id_ticket") + "\n" 
				            		 		+ "Limit: " + type);
				             }
				             
				             //Log.d("content", "OLAAAAAAAA" + "Ticket " + value.getInt("id_ticket") + "\n" 
				             //	+ "Type: " + value.getInt("type"));
				        }catch(Exception e){
				            Log.d("content", "Error on JSON 2: " + e.getMessage());
				        }

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
		
	}

}
