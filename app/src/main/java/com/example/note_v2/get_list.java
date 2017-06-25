package com.example.note_v2;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.chiralcode.colorpicker.ColorPickerDialog;
import com.chiralcode.colorpicker.ColorPickerDialog.OnColorSelectedListener;

public class get_list extends ListActivity {
	EditText editsearch;
	TextView textviewId;
	TextView texttime;
	EditText inputSearch;
	ListAdapter adapter;

	// Progress Dialog
	private ProgressDialog pDialog;
	int count=0;
	
	private Button showdate;
    private Calendar calendar;
    private int mYear, mMonth, mDay;
    private DatePickerDialog datePickerDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();
	JSONParser jsonParser = new JSONParser();
	ArrayList<HashMap<String, String>> notesList;

	// url to get all products list
	private String url_all_note = "http://61.220.27.214/thenote/get_user_note.php";
	private String url_change_color = "http://61.220.27.214/thenote/change_color.php";
	private static final String url_note_color = "http://61.220.27.214/thenote/url_note_color.php";
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_NOTE = "note";
	private static final String TAG_NOTEID = "noteid";
	private static final String TAG_TITLE = "title";
	private static final String TAG_DATE_UPDATED = "updated_at";
	private static final String TAG_DATE_CREATED = "created_at";
	JSONArray note = null;
	JSONArray color=null;

	 int Red;
	 int Green;
	 int Blue;


	class ChangeColor extends AsyncTask<String, String, String> {
	
		
		protected String doInBackground(String... args) {
		 
			// Building Parameters
			List<NameValuePair> params2 = new ArrayList<NameValuePair>();
			params2.add(new BasicNameValuePair("uid",UserInfo.userid));
			params2.add(new BasicNameValuePair("UserR", UserInfo.userR));
			params2.add(new BasicNameValuePair("UserG", UserInfo.userG));
			params2.add(new BasicNameValuePair("UserB", UserInfo.userB));
			// getting JSON Object
			// Note that create product url accepts POST method
			JSONObject json2 = jsonParser.makeHttpRequest(url_change_color,
					"POST", params2);
			
			// check log cat fro response
			Log.d("Create Response", json2.toString());
			String p=json2.toString();
			Log.d("Create Response", p);
			// check for success tag
			try {
				int success = json2.getInt(TAG_SUCCESS);
	
				if (success == 1) {
					
		            //finish();
					
					// closing this screen
				
				} else {
					// failed to create product
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
	
			return null;
		}
	
		
	
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notes_list);
		
		//����]�w
		  calendar = Calendar.getInstance();
	        mYear = calendar.get(Calendar.YEAR);
	        mMonth = calendar.get(Calendar.MONTH);
	        mDay = calendar.get(Calendar.DAY_OF_MONTH);
	        
	       
	        showdate = (Button)findViewById(R.id.showdate);
	        showdate.setOnClickListener(new OnClickListener(){
	            @Override
	            public void onClick(View view) {
	                showDialog(0);
	                datePickerDialog.updateDate(mYear, mMonth, mDay);
	                
	            }
	            
	        });
	
		Bundle bundle = getIntent().getExtras(); 
	     textviewId = (TextView)findViewById(R.id.userview);  
	  //   UserInfo.userid=bundle.getString("userids");//��USERID �g�iUSERINFO
	     textviewId.setText(UserInfo.userid);//�����e���W�@��Activity���Ѽ�
	   inputSearch=(EditText)findViewById(R.id.inputSearch);
		// Hashmap for ListView

		notesList = new ArrayList<HashMap<String, String>>();
		

		// Loading products in Background Thread
		new LoadAllNotes().execute();
		 ListView lv = getListView();
         adapter = new SimpleAdapter(
					get_list.this, notesList,
					R.layout.list_item, new String[] { TAG_NOTEID,
							TAG_TITLE,TAG_DATE_CREATED},
					new int[] { R.id.noteid, R.id.product ,R.id.time});
			// updating listview
			setListAdapter(adapter);
	        int Red=Integer.parseInt(UserInfo.userR);
	        int Green=Integer.parseInt(UserInfo.userG);
	        int Blue=Integer.parseInt(UserInfo.userB);
			lv.setBackgroundColor(Color.rgb(Red,Green,Blue));
		
		// Get listview
	
		
		// on selecting single product
		// launching Edit Product Screen
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				String noteid = ((TextView) view.findViewById(R.id.noteid)).getText()
						.toString();

				// Starting new intent
				Intent in = new Intent(getApplicationContext(),
						edit_list.class);
				// sending pid to next activity
				in.putExtra(TAG_NOTEID, noteid);
				
				// starting new activity and expecting some response back
				startActivityForResult(in, 100);
			}
		});
		}
	@Override
	protected void onDestroy() {
		pDialog.dismiss();
		super.onDestroy();
	}
	//showdate
		@Override
		 protected Dialog onCreateDialog(int id) {
		     datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
		            @Override
		            public void onDateSet(DatePicker view, int year, int month,
		                    int day) {
		                mYear = year;
		                mMonth = month;
		                mDay = day;
		                inputSearch.setText(setDateFormat(year,month,day)); 
		            }
		            
		        }, mYear,mMonth, mDay);
		        
		        return datePickerDialog;        
		 }
		
		//showdate
		private String setDateFormat(int year,int monthOfYear,int dayOfMonth){
			
			
	        return String.valueOf(year) + "-"
	        + String.valueOf(String.valueOf(monthOfYear + 1).length()==1?"0"+(monthOfYear + 1):monthOfYear + 1) + "-"
	        + String.valueOf(String.valueOf(dayOfMonth).length()==1?"0"+(dayOfMonth):dayOfMonth);
	    }

	// Response from Edit Product Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received 
			// means user edited/deleted product
			// reload this screen again
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}
	public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater menuInflater = getMenuInflater();
		getMenuInflater().inflate(R.menu.main, menu);
	
        return true;
    }
	@SuppressWarnings("ResourceType")/*inputSearch.setVisibility(0);
			showdate.setVisibility(0);*/
	public void clickMenuItem(MenuItem item) {
        // �ϥΰѼƨ��o�ϥΪ̿�ܪ���涵�ؤ���s��
        int itemId = item.getItemId();
 
        // �P�_�Ӱ��椰��u�@�A�ثe�٨S���[�J�ݭn���檺�u�@
        switch (itemId) {
        case R.id.select_color:
        	showColorPickerDialogDemo();
        	break;

        case R.id.add_item:
        	Intent intent = new Intent(this, MainActivity.class);
        	 Bundle bundle=new Bundle();
	          bundle.putString("userids", UserInfo.userid);
		      intent.putExtras(bundle); 
		       startActivity(intent);  
		       finish();
            break;               
        case R.id.delete_item:
        	break;
        case R.id.search_item:

        	inputSearch.setVisibility(0);
        	showdate.setVisibility(0);
        	
        	inputSearch.addTextChangedListener(new TextWatcher() {
   		     
    		    @Override
    		    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
    		        // When user changed the Text
    		        ((SimpleAdapter) get_list.this.adapter).getFilter().filter(cs);   
    		    }
    		     
    		    @Override
    		    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
    		            int arg3) {
    		        // TODO Auto-generated method stub
    		         
    		    }
    		     
    		    @Override
    		    public void afterTextChanged(Editable arg0) {
    		        // TODO Auto-generated method stub                          
    		    }
    		});
            break;
     
        }

	}
    private void showColorPickerDialogDemo() {

        int initialColor = Color.WHITE;

        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, initialColor, new OnColorSelectedListener() {

        	
            @Override
            public void onColorSelected(int color) {
                showToast(color);
           
               new ChangeColor().execute();
            }
            

        });
        colorPickerDialog.show();

    }

    /**
     * Displays Toast with RGB values of given color.
     * 
     * @param color the color
     */
    private void showToast(int color) {
        String rgbString = "R: " + Color.red(color) + " B: " + Color.blue(color) + " G: " + Color.green(color);
  
        UserInfo.userR = String.valueOf(Color.red(color)); 
        UserInfo.userG = String.valueOf(Color.green(color)); 
        UserInfo.userB = String.valueOf(Color.blue(color)); 
        
       int Red=Integer.parseInt(UserInfo.userR) ;
      int Green=Integer.parseInt(UserInfo.userG) ;
       int Blue=Integer.parseInt(UserInfo.userB) ;
        Toast.makeText(this, rgbString, Toast.LENGTH_SHORT).show();
        ListView lv = getListView();
        adapter = new SimpleAdapter(
					get_list.this, notesList,
					R.layout.list_item, new String[] { TAG_NOTEID,
							TAG_TITLE,TAG_DATE_CREATED},
					new int[] { R.id.noteid, R.id.product ,R.id.time});
			// updating listview
			setListAdapter(adapter);
			lv.setBackgroundColor(Color.rgb(Red,Green,Blue));
    }

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllNotes extends AsyncTask<String, String, String> {


		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(get_list.this);
			pDialog.setMessage("Loading Note. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		@SuppressWarnings("unused")
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
      
			
			params.add(new BasicNameValuePair("userid", UserInfo.userid));
			JSONObject json = jParser.makeHttpRequest(url_all_note, "GET", params);
		
			// Check your log cat for JSON reponse
			Log.d("All Products: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
				note = json.getJSONArray(TAG_NOTE);
				// looping through All Products
					for (int i = 0; i < note.length(); i++) {
						JSONObject c = note.getJSONObject(i);
						
						// Storing each json item in variable
						String id = c.getString(TAG_NOTEID);
						String name = c.getString(TAG_TITLE);
						String time = c.getString(TAG_DATE_UPDATED);
						String timecreated = c.getString(TAG_DATE_CREATED);
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();
					
						// adding each child node to HashMap key => value
						map.put(TAG_NOTEID, id);
						map.put(TAG_TITLE, name);
						map.put(TAG_DATE_CREATED, timecreated);
						// adding HashList to ArrayList
						notesList.add(map);
					}
				} else {
					 Bundle bundle=new Bundle();
					// no products found
					// Launch Add New product Activity
					Intent i = new Intent(getApplicationContext(),
							MainActivity.class);
					bundle.putString("userids",UserInfo.userid);
					 i.putExtras(bundle);	
					// Closing all previous activities
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					 
			         adapter = new SimpleAdapter(
								get_list.this, notesList,
								R.layout.list_item, new String[] { TAG_NOTEID,
										TAG_TITLE,TAG_DATE_CREATED},
								new int[] { R.id.noteid, R.id.product ,R.id.time});
						// updating listview
						setListAdapter(adapter);
				
				}
			});

		}
		
	    }
	
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
						String p=json.toString();
						Log.d("Single Product Details", p);
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received product details
		
							color = json.getJSONArray("users");
						 
								JSONObject c = color.getJSONObject(0);
								// Storing each json item in variable
								UserInfo.userR= c.getString("UserR");
								UserInfo.userG= c.getString("UserG");
								UserInfo.userB= c.getString("UserB");
	
								 ListView lv = getListView();
						         adapter = new SimpleAdapter(
											get_list.this, notesList,
											R.layout.list_item, new String[] { TAG_NOTEID,
													TAG_TITLE,TAG_DATE_CREATED},
											new int[] { R.id.noteid, R.id.product ,R.id.time});
						         Log.d("Single Product Details",UserInfo.userR);
									Red=Integer.parseInt(UserInfo.userR);
							        Green=Integer.parseInt(UserInfo.userG);
							       Blue=Integer.parseInt(UserInfo.userB);
									lv.setBackgroundColor(Color.rgb(Red,Green,Blue));
							
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
	
	
	}

	public void aboutApp(View view) {
	        Intent intent = new Intent(this, AboutActivity.class);	 
	        startActivity(intent);
	    }

	}
