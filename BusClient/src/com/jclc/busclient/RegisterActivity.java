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
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends LoginActivity {
    
    private EditText username;
    private EditText password;
    private EditText confirmpass;
    private EditText name;
    private EditText cardnumber;
    private EditText carddate;
    private EditText cardtype;
    
    
    @Override
    public void onCreate(Bundle savedInstance) {
   	 super.onCreate(savedInstance);
   	 this.setContentView(R.layout.activity_register);
   	 
   	 username = (EditText)findViewById(R.id.editText1);
   	 password = (EditText)findViewById(R.id.editText2);
   	 confirmpass = (EditText)findViewById(R.id.editText7);
   	 name = (EditText)findViewById(R.id.editText3);
   	 cardnumber = (EditText)findViewById(R.id.editText4);
   	 carddate = (EditText)findViewById(R.id.editText5);
   	 cardtype = (EditText)findViewById(R.id.editText6);
    }
    
    public void register(View v) {
   	 if (password.equals(confirmpass)){
   	 new Register().execute();
   	 }else{
   		 Toast.makeText(RegisterActivity.this,"Passwords must match!",Toast.LENGTH_LONG).show();
   	 }
    }
    
    public class Register extends AsyncTask<Object, Object, Object> {

   	 @Override
   	 protected Object doInBackground(Object... params) {
   		
   		 JSONObject json = new JSONObject();
   		 // Create a new HttpClient and Post Header
   		 HttpClient httpclient = new DefaultHttpClient();
   		 HttpPost httppost = new HttpPost("http://192.168.102.50:1122/register");

   		 try {
   			 try {
   				 json.put("username", username.getText().toString());
   				 json.put("password", password.getText().toString());
   				 json.put("name", name.getText().toString());
   				 json.put("cc_number", cardnumber.getText().toString());
   				 json.put("cc_date", carddate.getText().toString());
   				 json.put("cc_type", cardtype.getText().toString());
   				 
   			 } catch (JSONException e1) {
   				 Log.d("register", "Error on JSON: " + e1.getMessage());
   			 }
   			 StringEntity se = new StringEntity(json.toString());
   			 se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,"application/json"));
   			 httppost.setEntity(se);
   			 Log.d("SENDING", json.toString());

   			 // Execute HTTP Post Request
   			 try {
   				 HttpResponse response = httpclient.execute(httppost);
   				 if (response != null) {
   					 InputStream in = response.getEntity().getContent();

   					 try {
   						 Log.d("CENAS", convertStreamToString(in));
   						 JSONObject ans = new JSONObject(convertStreamToString(in).trim());
   						 String resp = ans.getString("response");
   						 if (resp.contentEquals("OK")){
   							 in.close();
   						 }else {
   							 in.close();
   						 }
   						 
   					 } catch (JSONException e) {
   						 Log.d("register", "Error on JSON: " + e.getMessage());
   					 }
   				 }

   			 } catch (ClientProtocolException e) {
   				 Log.d("register", "Error on ClientProtocol: " + e.getMessage());
   			 } catch (IOException e) {
   				 Log.d("register", "Error on IO: " + e.getMessage());
   			 }

   		 } catch (IOException e) {
   			 Log.d("register", "Error on IO: " + e.getMessage());
   		 }
   		 return null;
   	 }

   	 protected void onPostExecute(Boolean result) {

   	 }

    }
    
    public String convertStreamToString(InputStream is) throws IOException {
   	 if (is != null) {
   		 Writer writer = new StringWriter();
   		 char[] buffer = new char[1024];

   		 try {
   			 Reader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
   			 int n;

   			 while ((n = reader.read(buffer)) != -1) {
   				 writer.write(buffer, 0, n);
   			 }
   		 } finally {
   			 is.close();
   		 }
   		 return writer.toString();
   	 } else {
   		 return "";
   	 }
    }

}


