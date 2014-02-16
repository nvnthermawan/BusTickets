package com.jclc.busclient;

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
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class BuyTicketsActivity extends Activity {

	int id_user = -1;
	
	int t1 = -1;
	int t2 = -1;
	int t3 = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buy_tickets);
		
		Bundle b = getIntent().getExtras();
		
		if(b.containsKey("Iduser")){
			id_user = b.getInt("Iduser");
			Log.d("content","ON BUY TICKETS: "+ id_user);
		}
		
		
		final SeekBar seekBar = (SeekBar)findViewById(R.id.seekBar1);
		seekBar.setProgress(0);
		seekBar.incrementProgressBy(0);
		seekBar.setMax(10);
		final TextView seekBarValue = (TextView)findViewById(R.id.seekBar1Value);
		seekBarValue.setText("0");
		
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		        seekBarValue.setText(String.valueOf(progress));
		    }

		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) {

		    }

		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) {

		    }
		});
		
		
		final SeekBar seekBar2 = (SeekBar)findViewById(R.id.seekBar2);
		seekBar2.setProgress(0);
		seekBar2.incrementProgressBy(0);
		seekBar2.setMax(10);
		final TextView seekBarValue2 = (TextView)findViewById(R.id.seekBar2Value);
		seekBarValue2.setText("0");
		
		seekBar2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		        seekBarValue2.setText(String.valueOf(progress));
		    }

		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) {

		    }

		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) {

		    }
		});
		
		
		final SeekBar seekBar3 = (SeekBar)findViewById(R.id.seekBar3);
		seekBar3.setProgress(0);
		seekBar3.incrementProgressBy(0);
		seekBar3.setMax(10);
		final TextView seekBarValue3 = (TextView)findViewById(R.id.seekBar3Value);
		seekBarValue3.setText("0");
		
		seekBar3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		        seekBarValue3.setText(String.valueOf(progress));
		    }

		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar) {

		    }

		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) {

		    }
		});
		
		
		Button buttonBuyTickets = (Button)findViewById(R.id.buttonBuyTickets2);
		buttonBuyTickets.setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Log.d("content", "T1: " + seekBar.getProgress());
						t1 = seekBar.getProgress();
						t2 = seekBar2.getProgress();
						t3 = seekBar3.getProgress();
						
						if(t1 >= 0 && t2 >= 0 && t3 >= 0){
							BuyTickets bt = new BuyTickets();
							bt.execute();
						}
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.buy_tickets, menu);
		return true;
	}
	
	public class BuyTickets extends AsyncTask<Object, Object, Object>{

		String sresponse = "error";
		
		boolean flagbought = false;
		
		int t1_curr = -1;
		int t2_curr = -1;
		int t3_curr = -1;
		
		@Override
		protected Object doInBackground(Object... params) {
			postData();
			return null;
		}
		
		protected void onPostExecute(Object result)
		{
			if(t1_curr != -1 && t2_curr != -1 && t3_curr != -1){
				final TextView limit1 = (TextView)findViewById(R.id.LimitValue1);
				limit1.setText("of " + t1_curr);
				final TextView limit2 = (TextView)findViewById(R.id.LimitValue2);
				limit2.setText("of " + t2_curr);
				final TextView limit3 = (TextView)findViewById(R.id.LimitValue3);
				limit3.setText("of " + t3_curr);
			}
			if(flagbought){
				Toast.makeText(getApplicationContext(), "All Tickets Bought!", Toast.LENGTH_LONG).show();
			}
		}

		public void postData() {
		    // Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost("http://192.168.102.50:1122/buyTickets");

		    try {
		        // Add your data
		    	JSONObject holder = new JSONObject();

		    	try {
		    		holder.put("id_user", id_user);
		    		holder.put("t1", t1);
		    		holder.put("t2", t2);
		    		holder.put("t3", t3);
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
					
					if(holder2.has("response")){
						if(holder2.getString("response").equals("OK")){
							flagbought = true;
						}
						else if(holder2.getString("response").equals("NOK")){
							t1_curr = holder2.getInt("t1_space");
							t2_curr = holder2.getInt("t2_space");
							t3_curr = holder2.getInt("t3_space");
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
