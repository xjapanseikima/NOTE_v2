package com.example.note_v2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.note_v2.AndroidMultiPartEntity.ProgressListener;


public class edit_list extends Activity {

	EditText txttitle;
	EditText txtcontent;
	EditText txtTest;
	ImageButton btntakepic;
	ImageView editpicView;
	Button btnSave;
	Button btnDelete;
	Button	btnback;
    TextView update_view;
	String noteid;
	EditText txtTEST;
    static String  PictureFilename;
	// Progress Dialog
	private ProgressDialog pDialog;
	String MY_PIC_URL;
	 private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	// JSON parser class
	JSONParser jsonParser = new JSONParser();
	private static String mediaFileName;
	 private Uri fileUri; 
	 private TextView txtPercentage;
	 long totalSize = 0;
	 
	
	 private ProgressBar progressBar;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private static final String TAG = edit_list.class.getSimpleName();
	// single product url
	private static final String url_note_detials = "http://120.110.112.154/thenote/note_details.php";

	// url to update product
	private static final String url_update_note = "http://120.110.112.154/thenote/update_note.php";
	
	// url to delete product
	private static final String url_delete_note = "http://120.110.112.154/thenote/delete_note.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_NOTE = "note";
	private static final String TAG_NOTEID = "noteid";//pid
	private static final String TAG_CONTENT = "content";//description
	private static final String TAG_TITLE = "title";
	private static final String TAG_DATE_UPDATED = "updated_at";
	private static final String TAG_PIC_URL ="url";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_list);
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
		// save button
	    btnSave = (Button) findViewById(R.id.btnconfirm);
	    btntakepic= (ImageButton) findViewById(R.id.take_picture);
	    editpicView=(ImageView) findViewById(R.id. edit_picture);
	    txtPercentage = (TextView) findViewById(R.id.txtPercentage);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		btnback = (Button) findViewById(R.id.btback);
		btnDelete = (Button) findViewById(R.id.btnnotedelete);
		// getting product details from intent
		Intent i = getIntent();
		
		// getting product id (pid) from intent
		noteid = i.getStringExtra(TAG_NOTEID);

		// Getting complete product details in background thread
		new GetNoteDetails().execute();
	
		btntakepic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {				
				  captureImage();       
				    	    }
			
		});
   
		 
		// save button click event
		btnSave.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(UserInfo.fileUri==null){				
					
					if(UserInfo.picname!=null){
					 new OnlySaveNoteDetails().execute();
					}
					
				}
				else if(UserInfo.fileUri!=null)
				{ 
					
					new UploadFileToServer().execute();
					 UserInfo.fileUri=null;
					
					
				
				}
				
				
			}
		});

		// Delete button click event
		btnDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// deleting product in background thread
				new DeleteNote().execute();
			}
		});
		btnback.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {				
				Intent intent = new Intent();
	            intent.setClass(edit_list.this,get_list.class);
	            startActivity(intent);
	            finish();
				
				    	        
				    	    }
			
		});
		

	}
	public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater menuInflater = getMenuInflater();
		getMenuInflater().inflate(R.menu.main2, menu);
	
        return true;
    }
	 public void clickFunction(View view) {
	        int id = view.getId();
	 
	        switch (id) {
	        case R.id.take_picture:

	            break;
	        case R.id.record_sound:
	            break;
	        case R.id.set_location:
	            break;
	        case R.id.select_color:
	            break;
	        }
	        }
	 public void clickMenuItem(MenuItem item) {
	        // 使用參數取得使用者選擇的選單項目元件編號
	        int itemId = item.getItemId();
	 
	        // 判斷該執行什麼工作，目前還沒有加入需要執行的工作
	        switch (itemId) {
	        

	        
	        case R.id.revert_item:
	        	Intent intent = new Intent();
	            intent.setClass(edit_list.this,get_list.class);
	            startActivity(intent);
	            finish();
	            break;
	        case R.id.delete_item:
	        	new DeleteNote().execute();
	        	break;
	        case R.id.ok_item:
	        	if(editpicView.getDrawable() == null){
					new SaveNoteDetails().execute();
				}
				else{new UploadFileToServer().execute();}
				
	            break;
	        }

		}
	 private void captureImage() {
	        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	 
	        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
	 
	        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
	 
	        // start the image capture Intent
	        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
	    }
	 public Uri getOutputMediaFileUri(int type) {
	        return Uri.fromFile(getOutputMediaFile(type));
	    }
	  /**
	 * Receiving activity result method will be called after closing the camera
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // if the result is capturing Image
	    if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            
	        	
	        	// successfully captured the image
	            // launching upload activity
	        	BitmapFactory.Options options = new BitmapFactory.Options();
	
				// down sizing image as it throws OutOfMemory Exception for larger
				// images
				options.inSampleSize = 8;
				
				UserInfo.fileUri=fileUri.getPath();
				final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);
	
				 editpicView.setImageBitmap(bitmap);
	      	           	
	        } else if (resultCode == RESULT_CANCELED) {
	            
	        	// user cancelled Image capture
	            Toast.makeText(getApplicationContext(),
	                    "User cancelled image capture", Toast.LENGTH_SHORT)
	                    .show();
	        
	        } else {
	            // failed to capture image
	            Toast.makeText(getApplicationContext(),
	                    "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
	                    .show();
	        }
	    
	    } 
	}
	private static File getOutputMediaFile(int type) {
	    	 
	        // External sdcard location
	        File mediaStorageDir = new File(
	                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
	                Config.IMAGE_DIRECTORY_NAME);
	 
	        // Create the storage directory if it does not exist
	        if (!mediaStorageDir.exists()) {
	            if (!mediaStorageDir.mkdirs()) {
	                Log.d(TAG, "Oops! Failed create "
	                        + Config.IMAGE_DIRECTORY_NAME + " directory");
	                return null;
	            }
	        }
	 
	        // Create a media file name
	        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
	                Locale.getDefault()).format(new Date());
	        File mediaFile;
	        if (type == MEDIA_TYPE_IMAGE) {
	            mediaFile = new File(mediaStorageDir.getPath() + File.separator
	                    + "IMG_" + timeStamp + ".jpg");
	           mediaFileName = "IMG_" + timeStamp + ".jpg";//圖片名稱
	        } else {
	            return null;
	        }
	 
	        return mediaFile;
	    }
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	 
	/**
	 * Background Async Task to Get complete product details
	 * */
	class GetNoteDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(edit_list.this);
			pDialog.setMessage("Loading Note ,please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

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
						params.add(new BasicNameValuePair("noteid", noteid));

						// getting product details by making HTTP request
						// Note that product details url will use GET request
						JSONObject json = jsonParser.makeHttpRequest(
								url_note_detials, "GET", params);

						// check your log for json response
						Log.d("Single Product Details", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received product details
							JSONArray productObj = json
									.getJSONArray(TAG_NOTE); // JSON Array
							
							// get first product object from JSON Array
							JSONObject product = productObj.getJSONObject(0);

							// product with this pid found
							// Edit Text
							txttitle = (EditText) findViewById(R.id.title_text);
							update_view = (TextView) findViewById(R.id.update_date);
							txtcontent = (EditText) findViewById(R.id.content_text);
							
							// display product data in EditText
							txttitle.setText(product.getString(TAG_TITLE));
							txtcontent.setText(product.getString(TAG_CONTENT));
					        PictureFilename=product.getString(TAG_PIC_URL);
					        UserInfo.picname=product.getString(TAG_PIC_URL);
					        UserInfo.picname=PictureFilename;
					        if(PictureFilename.length()<=1){}
					        else{
					        MY_PIC_URL=Config.FILE_URL+PictureFilename;
							  
							new DownloadImageTask((ImageView) findViewById(R.id.edit_picture))
					        .execute( MY_PIC_URL);
					        }
							if(product.getString(TAG_DATE_UPDATED)=="null")
							{update_view .setText("你尚無新修改時間");}
							else{
							update_view .setText(product.getString(TAG_DATE_UPDATED));
							}
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

	/**
	 * Background Async Task to  Save product Details
	 * */
	class SaveNoteDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(edit_list.this);
			pDialog.setMessage("Saving Note...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Saving product
		 * */
		protected String doInBackground(String... args) {

			// getting updated data from EditTexts
			String title = txttitle.getText().toString();
			String content = txtcontent.getText().toString();
			String picUrl=mediaFileName;
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_NOTEID,  noteid));
			params.add(new BasicNameValuePair(TAG_TITLE, title));
			params.add(new BasicNameValuePair(TAG_CONTENT, content));
			 params.add(new BasicNameValuePair("picUrl",picUrl));
			// sending modified data through http request
			// Notice that update product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_update_note,
					"POST", params);

			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					// successfully updated
					Intent i = getIntent();
					// send result code 100 to notify about product update
					setResult(100, i);
					finish();
				} else {
					// failed to update product
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
			// dismiss the dialog once product uupdated
			pDialog.dismiss();
		}
	}

	/*****************************************************************
	 * Background Async Task to Delete Product
	 * */
	class DeleteNote extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(edit_list.this);
			pDialog.setMessage("Deleting NOte...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Deleting product
		 * */
		protected String doInBackground(String... args) {

			// Check for success tag
			int success;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("noteid", noteid));

				// getting product details by making HTTP request
				JSONObject json = jsonParser.makeHttpRequest(
						url_delete_note, "POST", params);

				// check your log for json response
				Log.d("Delete Product", json.toString());
				
				// json success tag
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// product successfully deleted
					// notify previous activity by sending code 100
					Intent i = getIntent();
					// send result code 100 to notify about product deletion
					setResult(100, i);
					finish();
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
			// dismiss the dialog once product deleted
			pDialog.dismiss();

		}
		

	}
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		  ImageView bmImage;

		  public DownloadImageTask(ImageView bmImage) {
		      this.bmImage = bmImage;
		  }

		  protected Bitmap doInBackground(String... urls) {
		      String urldisplay = urls[0];
		      Bitmap mIcon11 = null;
		      try {
		        InputStream in = new java.net.URL(urldisplay).openStream();
		        mIcon11 = BitmapFactory.decodeStream(in);
		      } catch (Exception e) {
		          Log.e("Error", e.getMessage());
		          e.printStackTrace();
		      }
		      return mIcon11;
		  }

		  protected void onPostExecute(Bitmap result) {
		      bmImage.setImageBitmap(result);

		  }
		}

	/**
	 * Uploading the file to server
	 * */
	private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
		@Override
		protected void onPreExecute() {
			// setting progress bar to zero
			progressBar.setProgress(0);
			super.onPreExecute();
			
		}
	
		@Override
		protected void onProgressUpdate(Integer... progress) {
			// Making progress bar visible
			progressBar.setVisibility(View.VISIBLE);
	
			// updating progress bar value
			progressBar.setProgress(progress[0]);
	
			// updating percentage value
			txtPercentage.setText(String.valueOf(progress[0]) + "%");
	
		}
	
		@Override
		protected String doInBackground(Void... params) {
		
				
			return uploadFile();
		
		}
	@SuppressWarnings("deprecation")
		private String uploadFile() {
			String responseString = null;
	
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);
	
			try {
				AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
						new ProgressListener() {
	
							@Override
							public void transferred(long num) {
								publishProgress((int) ((num / (float) totalSize) * 100));
							}
						});
				
		
		
				File sourceFile = new File(fileUri.getPath());
	
				// Adding file data to http body
				entity.addPart("image", new FileBody(sourceFile));
	
				
	
				totalSize = entity.getContentLength();
				httppost.setEntity(entity);
	
				// Making server call
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity r_entity = response.getEntity();
	
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode == 200) {
					// Server response
					responseString = EntityUtils.toString(r_entity);
				} else {
					responseString = "Error occurred! Http Status Code: "
							+ statusCode;
				}
	
				} catch (ClientProtocolException e) {
				responseString = e.toString();
			} catch (IOException e) {
				responseString = e.toString();
			}
	
			return responseString;
	
			}
	
		@Override
		protected void onPostExecute(String result) {
			Log.e(TAG, "Response from server: " + result);
	
			// showing the server response in an alert dialog
			new SaveNoteDetails().execute();
			super.onPostExecute(result);
			Intent intent = new Intent();
			intent.setClass(edit_list.this,get_list.class);
		   Bundle bundle=new Bundle();
	       bundle.putString("userids",UserInfo.userid);
	       startActivity(intent);
	       finish();
		}
	
	}

	/**
	 * Background Async Task to  Save product Details
	 * */
	class OnlySaveNoteDetails extends AsyncTask<String, String, String> {
	
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(edit_list.this);
			pDialog.setMessage("Saving Note...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
	
		/**
		 * Saving product
		 * */
		protected String doInBackground(String... args) {
	
			// getting updated data from EditTexts
			String title = txttitle.getText().toString();
			String content = txtcontent.getText().toString();
			String picname= UserInfo.picname;
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(TAG_NOTEID,  noteid));
			params.add(new BasicNameValuePair(TAG_TITLE, title));
			params.add(new BasicNameValuePair(TAG_CONTENT, content));
		  
			params.add(new BasicNameValuePair("picUrl", UserInfo.picname));
			
			// sending modified data through http request
			// Notice that update product url accepts POST method
			JSONObject json = jsonParser.makeHttpRequest(url_update_note,
					"POST", params);
	
			// check json success tag
			try {
				int success = json.getInt(TAG_SUCCESS);
				
				if (success == 1) {
					// successfully updated
					Intent i = getIntent();
					// send result code 100 to notify about product update
					setResult(100, i);
					finish();
				} else {
					// failed to update product
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
			// dismiss the dialog once product uupdated
			pDialog.dismiss();
		}
	}
}
