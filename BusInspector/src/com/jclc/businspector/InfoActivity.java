package com.jclc.businspector;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class InfoActivity extends Activity {

	String response = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_info);
		
		Bundle b = getIntent().getExtras();
		
		if(b.containsKey("Response")){
			response = b.getString("Response");
			Log.d("content",response);
		}
		else {
			Log.d("InfoActivity", "No RESPONSE sent!");
			return;
		}
		
		Runnable updateTextView = new Runnable() {

		    public void run() {
		    	TextView msg = (TextView) findViewById(R.id.textview1);
				msg.setText(response);
		    }
		};
		updateTextView.run();
		
	}

	@Override
	public void onBackPressed() {
		startActivityForResult(new Intent(this, MainActivity.class), 0);
		finish();
	    return;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.info, menu);
		return true;
	}
	
	public void changeIntent(View view){ 
		startActivityForResult(new Intent(this, MainActivity.class), 0);
		finish();
	}

}
