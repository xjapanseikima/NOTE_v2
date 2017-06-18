package com.example.note_v2;


import info.androidhive.loginandregistration.helper.SessionManager;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class EnterActivity extends Activity {

	private TextView txtName;
	private TextView txtEmail;
	private Button btnLogout;
	private Button btnWriteNote;
	private TextView userid;
	private TextView txtSpeechInputa;
	private ImageButton btnSpeak;
	private final int REQ_CODE_SPEECH_INPUT = 100;
	String uid;
	String voice;
	JSONParser jsonParser = new JSONParser();
	private static final String TAG_LOGIN_ID = "uid";
	private ProgressDialog pDialog;
	private SessionManager session;
	private static final String url_note_color = "http://120.110.112.154/thenote/note_colors.php";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_USERS = "users";
	private static final String TAG_USERID = "uid";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		txtSpeechInputa = (TextView) findViewById(R.id.txtSpeechInput);
		btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
		
		txtEmail = (TextView) findViewById(R.id.email);
		userid = (TextView) findViewById(R.id.useridview);//�x�suserid
		btnLogout = (Button) findViewById(R.id.btnLogout);
		Bundle bundle = getIntent().getExtras();
		txtEmail.setText(bundle.getString("email"));//�����e���W�@��Activity���Ѽ�
		btnWriteNote= (Button) findViewById(R.id.writenote);
		txtName= (TextView) findViewById(R.id.name);
		String email = txtEmail.getText().toString();//email�O�h�P�_��Ʈw�� �ϥΪ�
         new SigninActivity(txtName,userid,0).execute(email);//��mail�h���Ʈw�̬۹����� ���(�]�tuserid ,�m�W)
		StrictMode
	    .setThreadPolicy(new StrictMode.ThreadPolicy.Builder()  
	    .detectDiskReads()  
	    .detectDiskWrites()  
	    .detectNetwork()   // or .detectAll() for all detectable problems  
	    .penaltyLog()  
	    .build());  
	     StrictMode
	    .setVmPolicy(new StrictMode.VmPolicy.Builder()  
	    .detectLeakedSqlLiteObjects()  
	    .detectLeakedClosableObjects()  
	    .penaltyLog()  
	    .penaltyDeath()  
	    .build());

	


		// session manager
		session = new SessionManager(getApplicationContext());

		if (!session.isLoggedIn()) {
			logoutUser();
		}

		// Fetching user details from sqlite





		// Logout button click event
		btnLogout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				logoutUser();
			}
		});

		btnSpeak.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				promptSpeechInput();
				
		
			}
		});

	
		btnWriteNote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
					
					Intent intent = new Intent();
		            intent.setClass(EnterActivity.this,get_list.class); 
		          Bundle bundle=new Bundle();
		         UserInfo.userid=userid.getText().toString();
		          bundle.putString("userids", UserInfo.userid);
			      intent.putExtras(bundle); 
			        startActivity(intent);    //�}��Activity
					
				}
		});
		
		
	}

	
	
	/**
	 * Logging out the user. Will set isLoggedIn flag to false in shared
	 * preferences Clears the user data from sqlite users table
	 * */
	private void logoutUser() {
		session.setLogin(false);


		// Launching the login activity
		Intent intent = new Intent(EnterActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	private void promptSpeechInput() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en_US");
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
				"Say something");
		try {
			startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
		} catch (ActivityNotFoundException a) {
			Toast.makeText(getApplicationContext(),
					"not support",
					Toast.LENGTH_SHORT).show();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case REQ_CODE_SPEECH_INPUT: {
			if (resultCode == RESULT_OK && null != data) {

				ArrayList<String> result = data
						.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
				
				txtSpeechInputa.setText(result.get(0));

			}
			

			break;
		}

		}if("hello".equals(txtSpeechInputa.getText())){
			

			
			
			Intent intent = new Intent();
            intent.setClass(EnterActivity.this,get_list.class); 
          Bundle bundle=new Bundle();
         UserInfo.userid=userid.getText().toString();
          bundle.putString("userids", UserInfo.userid);
	      intent.putExtras(bundle); 
	        startActivity(intent);    //�}��Activity
			
			


}
		
	}

	

	class SigninActivity  extends AsyncTask<String,Void,String>{

		   private TextView userid,roleField;
		  
		   private int byGetOrPost = 0; 
		   //flag 0 means get and 1 means post.(By default it is get.)
		   public SigninActivity(
				   TextView roleField,TextView userid,int flag) {
		            this.roleField = roleField;
		            this.userid = userid;
		      byGetOrPost = flag;
		   }

		   protected void onPreExecute(){

		   }
		   @Override
		   protected String doInBackground(String... arg0) {
		      if(byGetOrPost == 0){ //means by Get Method
		         try{
		            String email = (String)arg0[0];	   
					String Login = "http://61.220.27.214/thenote/login_test.php?email="
		            +email;
		           
		            HttpClient client = new DefaultHttpClient();
		            HttpGet request = new HttpGet();
		            request.setURI(new URI(Login));
		            HttpResponse response = client.execute(request);
		           Scanner in=new Scanner(response.getEntity().getContent());
		           String userinfromation=	in.nextLine().replace("<br />", " ");
		            return userinfromation;
		      }catch(Exception e){
		         return new String("Exception: " + e.getMessage());
		      }
		      }
			return null;
		     
		   }
		   @Override
		   protected void onPostExecute(String result){
		    String userinfromation[]=result.split(" ");//�I�s php�̪�$result,�M��A�I�ssplit����Ƥ���
			   this.roleField.setText(userinfromation[0]);// user
		       this.userid.setText(userinfromation[1]);//userid
	
			
			       
		   }
		}



	/**
	 * Background Async Task to Get complete product details
	 * */
	class GetColor extends AsyncTask<String, String, String> {
	
		
	
		/**
		 * Getting product details in background thread
		 * */
		protected String doInBackground(String... params) {
	
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
			
	
				public void run() {
					// Check for success tag
					int success;
					try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("uid", UserInfo.userid));
	
						// getting product details by making HTTP request
						// Note that product details url will use GET request
						JSONObject json = jsonParser.makeHttpRequest(
							url_note_color, "GET", params);
	
						// check your log for json response
						Log.d("Single Product Details", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received product details
							JSONArray productObj = json
									.getJSONArray(UserInfo.userid); // JSON Array
							
							// get first product object from JSON Array
							JSONObject product = productObj.getJSONObject(0);
	                    UserInfo.userR=product.getString("UserR");
	                    UserInfo.userG=product.getString("UserG");
	                    UserInfo.userB=product.getString("UserB");
							
					
						}else{
							// product with pid not found
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});
	
			return null;
		}
	
	
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once got all details
			pDialog.dismiss();
		}
	}




}
