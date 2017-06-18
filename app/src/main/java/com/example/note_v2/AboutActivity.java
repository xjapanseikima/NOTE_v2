package com.example.note_v2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class AboutActivity extends Activity {
	 
	Button cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 取消元件的應用程式標題
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
        cancel= (Button) findViewById(R.id.cancel_item);
        
    
 
    // 結束按鈕
    cancel.setOnClickListener(new View.OnClickListener() {

		@Override
		public void onClick(View view) {				
			Intent intent = new Intent();
            intent.setClass(AboutActivity.this,get_list.class);
            finish();
			
			    	        
			    	    }
		
	});
 
}
}