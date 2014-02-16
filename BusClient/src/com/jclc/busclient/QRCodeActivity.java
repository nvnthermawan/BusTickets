package com.jclc.busclient;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QRCodeActivity extends Activity {

	String token = "";
	String date = "";

	int x = 400;
	int y = x;

	public void onCreate(Bundle savedInstance) {

		if(getIntent().hasExtra("UserTicket")){
			token = getIntent().getStringExtra("UserTicket");
		}
		else {
			Toast.makeText(getApplicationContext(), "User and/or Ticket data not available!", Toast.LENGTH_LONG).show();
			token = "-1,-1";
		}
		if(getIntent().hasExtra("Date")){
			date = getIntent().getStringExtra("Date");
		}

		super.onCreate(savedInstance);
		this.setContentView(R.layout.activity_qrcode);
		ImageView img = (ImageView) findViewById(R.id.imageView1);
		try {
			generateQRCode_general(token,img);
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Log.d("content", "Date: " + date);
		Runnable updateTextView = new Runnable() {

		    public void run() {
		    	TextView msg = (TextView) findViewById(R.id.textView1);
				msg.setText(date);
		    }
		};
		updateTextView.run();
	}

	public void generateQRCode_general(String data, ImageView img)throws WriterException {
		com.google.zxing.Writer writer = new QRCodeWriter();

		BitMatrix bm = writer.encode(data, BarcodeFormat.QR_CODE,x, y);
		Bitmap ImageBitmap = Bitmap.createBitmap(x, y,Config.ARGB_8888);

		for (int i = 0; i < x; i++) {
			for (int j = 0; j < y; j++) {
				ImageBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK: Color.WHITE);
			}
		}

		if (ImageBitmap != null) {
			img.setImageBitmap(ImageBitmap);
		} else {
			Toast.makeText(QRCodeActivity.this,"Uable to setImageBitmap \n ImageBitmap is NULL",Toast.LENGTH_LONG).show();
		}
	}

}



/*
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;

public class QRCodeActivity extends Activity {

	String ut = "";

	int size = 400;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qrcode);

		Bundle b = getIntent().getExtras();

		if(b.containsKey("UserTicket")){
			ut = b.getString("UserTicket");
			Log.d("qrcode", ut);
		}

		String address = "https://chart.googleapis.com/chart?";

		address += "cht=qr" + "&" + "chs=" + 400 + "x" + 400 + "&" + "chl=" + ut + "";

		final ImageView iv = (ImageView)findViewById(R.id.imageView1);

		URL url;
		InputStream content = null;
		try {
			url = new URL(address);
			content = (InputStream)url.getContent();
		} catch (MalformedURLException e) {
			Log.d("qrcode", "Error Malformerd URL: " + e.getMessage());
		} catch (IOException e) {
			Log.d("qrcode", "Error IO: " + e.getMessage());
		}

		Drawable d = Drawable.createFromStream(content , "src"); 
		iv.setImageDrawable(d);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.qrcode, menu);
		return true;
	}

}
 */