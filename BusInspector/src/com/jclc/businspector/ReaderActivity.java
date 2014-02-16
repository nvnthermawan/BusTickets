package com.jclc.businspector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

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
import android.content.Intent;
import android.util.Log;
import android.view.Menu;

public class ReaderActivity extends Activity {
	
	String response2 = "";
	
	int id_ticket = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reader);
		Intent intent = new Intent("com.google.zxing.client.android.SCAN"); 
		startActivityForResult(intent, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.reader, menu);
		return true;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    if (requestCode == 0) {
	        if (resultCode == RESULT_OK) {
	            // contents contains whatever was encoded
	            String contents = intent.getStringExtra("SCAN_RESULT");

	            // Format contains the type of code i.e. UPC, EAN, QRCode etc...
	            String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
	            Log.d("content","contents: " + contents);
	            Log.d("content","format: " + format);
	            
	            if(contents != null && contents.length()>=0 && contents.contains(",")){
	            	id_ticket = Integer.parseInt(contents.split(",")[0]);
	            }
	            else {
	            	id_ticket = -1;
	            }
 
	            IsTicketValid itv = new IsTicketValid();
	    		itv.execute();
	            
	        }
	    }

	}
	
	public void changeIntent(){
		Intent intent2 = new Intent(this, InfoActivity.class); 
		intent2.putExtra("Response", response2);
		startActivityForResult(intent2, 0);
	}
	
	public class IsTicketValid extends AsyncTask<Object, Object, Object>{

		String sresponse = "error";
		
		@Override
		protected Object doInBackground(Object... params) {
			postData();
			return null;
		}

		public void postData() {
		    // Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost("http://192.168.102.50:1122/isTicketValid");

		    try {
		        // Add your data
		    	JSONObject holder = new JSONObject();

		    	try {
		    		holder.put("id_ticket", id_ticket);
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
					
					String theresponse = holder2.getString("response");
					
					Log.d("content","RESPONSE: " + theresponse);
					
					if(theresponse.equals("OK")){
						response2 = "VALID TICKET!";
						if(holder2.has("date")){
							response2 += "\n Expires at " + holder2.getString("date");
						}
					}
					else {
						response2 = "TICKET IS NOT VALID!";
						if(holder2.has("date")){
							response2 += "\n Expired at " + holder2.getString("date");
						}
					}
					
					changeIntent();
					
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
