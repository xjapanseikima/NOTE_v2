package com.example.note_v2;


import com.example.note_v2.AndroidMultiPartEntity.ProgressListener;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private EditText mTitleText;
    private EditText mBodyText;
    private TextView txtSpeechInput;
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    View rootView;
    Button confirmButton;
    Button cancel;
    ImageButton btntakepic;
    ImageButton record;
    private ImageView imgPreview;
    private TextView UserTextId;
    JSONParser jsonParser = new JSONParser();
    public static FragmentManager fm;
    private static final String TAG = MainActivity.class.getSimpleName();
    long totalSize = 0;
    private TextView txtPercentage;
    private ProgressBar progressBar;
    private String filePath = null;
    private static String mediaFileName;
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri; // file url to store image/video
    private static String url_create_note = "http://61.220.27.214/thenote/create_note.php";
    private ProgressDialog pDialog;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.note_edit, container, false);
        return V;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_edit);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        record = (ImageButton) findViewById(R.id.record_sound);
        imgPreview = (ImageView) findViewById(R.id.picture);
        mTitleText = (EditText) findViewById(R.id.title_text);
        confirmButton = (Button) findViewById(R.id.ok_teim);
        mBodyText = (EditText) findViewById(R.id.content_body);
        cancel = (Button) findViewById(R.id.cancel_item);
        UserTextId = (TextView) findViewById(R.id.note_create_userid);
        Bundle bundle = getIntent().getExtras();
        UserTextId.setText(bundle.getString("userids"));
        btntakepic = (ImageButton) findViewById(R.id.take_picture);
        filePath = UserInfo.fileUri;
        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        confirmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (imgPreview.getDrawable() == null) {
                    new OnlyCreateNewNote().execute();
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, get_list.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("userids", UserInfo.userid);
                    startActivity(intent);
                    finish();
                } else {

                    new UploadFileToServer().execute();

                }
            }

        });
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, get_list.class);
                bundle.putString("userids", UserTextId.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }

        });


        btntakepic.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                captureImage();
            }

        });


    }

    public void clickFunction(View view) {
        int id = view.getId();

        switch (id) {

            case R.id.record_sound:
                break;
            case R.id.set_location:
                break;
            case R.id.set_alarm:
                break;
            case R.id.select_color:
                break;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        getMenuInflater().inflate(R.menu.main3, menu);

        return true;
    }

    public void clickMenuItem(MenuItem item) {
        // �ϥΰѼƨ��o�ϥΪ̿�ܪ���涵�ؤ���s��
        int itemId = item.getItemId();

        // �P�_�Ӱ��椰��u�@�A�ثe�٨S���[�J�ݭn���檺�u�@
        switch (itemId) {


            case R.id.revert_item:
                Bundle bundle = new Bundle();
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, get_list.class);
                bundle.putString("userids", UserTextId.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                break;
            case R.id.ok_item:
                if (imgPreview.getDrawable() == null) {
                    new OnlyCreateNewNote().execute();
                    Intent intent2 = new Intent();
                    intent2.setClass(MainActivity.this, get_list.class);
                    Bundle bundle2 = new Bundle();
                    bundle2.putString("userids", UserInfo.userid);
                    startActivity(intent2);
                    finish();
                } else
                    new UploadFileToServer().execute();

                break;
        }

    }


    /**
     * Launching camera app to capture image
     */
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
            mediaFileName = "IMG_" + timeStamp + ".jpg";//�Ϥ��W��
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }


    /**
     * Receiving activity result method will be called after closing the camera
     */
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

                UserInfo.fileUri = fileUri.getPath();
                final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

                imgPreview.setImageBitmap(bitmap);

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

        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // video successfully recorded
                // launching upload activity


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


    /**
     * Method to show alert dialog
     */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    class CreateNewNote extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */


        /**
         * Creating product
         */

        protected String doInBackground(String... args) {
            String title = mTitleText.getText().toString();
            String content = mBodyText.getText().toString();
            String userid = UserTextId.getText().toString();
            String picUrl = mediaFileName;
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("title", title));
            params.add(new BasicNameValuePair("content", content));
            params.add(new BasicNameValuePair("userid", userid));
            params.add(new BasicNameValuePair("picUrl", picUrl));
            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_note,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    finish();

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


    /**
     * Uploading the file to server
     */
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
            showAlert(result);
            super.onPostExecute(result);
            new CreateNewNote().execute();
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, get_list.class);
            Bundle bundle = new Bundle();
            bundle.putString("userids", UserInfo.userid);
            startActivity(intent);
            finish();
        }

    }


    class OnlyCreateNewNote extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         * */


        /**
         * Creating product
         */

        protected String doInBackground(String... args) {
            String title = mTitleText.getText().toString();
            String content = mBodyText.getText().toString();
            String userid = UserTextId.getText().toString();

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("title", title));
            params.add(new BasicNameValuePair("content", content));
            params.add(new BasicNameValuePair("userid", userid));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_create_note,
                    "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {

                    finish();

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
}





	


