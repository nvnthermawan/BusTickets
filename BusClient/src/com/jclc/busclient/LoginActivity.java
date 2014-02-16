package com.jclc.busclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

	int id_user=-1;

	//private static final String[] DUMMY_CREDENTIALS = new String[] {
	//	"foo@example.com:hello", "bar@example.com:world" };

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		// Set up the login form.
		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
		.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id,
					KeyEvent keyEvent) {
				if (id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		}/* else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}*/

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		HttpResponse response;

		@SuppressWarnings("unused")
		@Override
		protected Boolean doInBackground(Void... params) {

			int temp = -2;
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost request = new HttpPost("http://192.168.102.50:1122/login");
			// HttpResponse response = new HttpRes;

			// Add your data

			mPassword = md5(mPassword);

			JSONObject json = new JSONObject();
			try {
				json.put("username", mEmail);
				json.put("password",mPassword);
			} catch (JSONException e1) {
				Log.d("content", "Error on JSON: " + e1.getMessage());
			}

			if(json != null){
				Log.d("content", "JSON STRING: " + json.toString());
				String a= json.toString();
				StringEntity entity = null;
				try {
					entity = new StringEntity(a);
				} catch (UnsupportedEncodingException e) {
					Log.e("content", "LOGIN: Exception on Entity");
					//e.printStackTrace();
				}

				if(entity != null){
					request.setEntity(entity); 
					request.setHeader("Accept", "application/json");
					request.setHeader("Content-type", "application/json");
				}
			}
			else Log.d("content", "LOGIN: JSON null");

			try {
				response = httpclient.execute(request);

				String sresponse = "error";

				if(response != null){

					InputStream in = response.getEntity().getContent();
					sresponse = convertStreamToString(in);

					if(!sresponse.equals("error")){

						JSONObject jaux = new JSONObject(sresponse);
						if(jaux.has("id_user")) {
							temp = jaux.getInt("id_user");

							Log.d("content", "Response: " + temp);

							if(temp == -1)
								return false;
							else{
								id_user = temp;
								return true;
							}
						}

					}
				}

				Log.d("content", "Response (again): " + temp);

			} catch (ClientProtocolException e) {
				Log.e("Login", "ClientProtocolException: " + e.toString());

			} catch (IOException e) {
				//e.printStackTrace();
				Log.e("Login", "IOException: " + e.toString());
			} catch (JSONException e) {
				Log.e("Login", "JSONException: " + e.toString());
			} 

			return false;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) 
			{	
				Toast.makeText(getApplicationContext(), "Login Successeful!", Toast.LENGTH_LONG).show();

				launchlogin();



				finish();
			} else {
				Toast.makeText(getApplicationContext(), "Error on login! Wrong Username/Password!", Toast.LENGTH_LONG).show();
				/*mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
				 */
				//finish();

			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	public String md5(String str) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(str.getBytes());
			byte bytes[] = digest.digest();

			// Create Hex String
			StringBuilder s = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
				int parteBaixa = bytes[i] & 0xf;
				if (parteAlta == 0) s.append('0');
				s.append(Integer.toHexString(parteAlta | parteBaixa));
			}
			return s.toString();

		} catch (NoSuchAlgorithmException e) {
			Log.d("content", "Error creating MD5 password: " + e.getMessage());
		}
		return "";
	}

	public void launchlogin(){
		Intent intent = new Intent(this,LoggedActivity.class);
		intent.putExtra("Iduser", id_user);
		startActivity(intent);
	}

	public void register(View v){
		startActivity(new Intent(this,RegisterActivity.class));
	}
}
