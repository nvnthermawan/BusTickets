package com.jclc.busclient;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class LoggedActivity extends Activity {

	int id_user = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logged);
		
		Bundle b = getIntent().getExtras();
		
		if(b.containsKey("Iduser")){
			id_user = b.getInt("Iduser");
			Log.d("content","ON LOGGED IN: "+ id_user);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.logged, menu);
		return true;
	}
	
	
	public void buyTickets(View view) {
		Intent intent = new Intent(this,BuyTicketsActivity.class);
		intent.putExtra("Iduser", id_user);
		startActivity(intent);
	}
	
	public void viewTickets(View view) {
		Intent intent = new Intent(this,ViewTicketsActivity.class);
		intent.putExtra("Iduser", id_user);
		startActivity(intent);
	}

}
