package com.tcl.roselauncher.ui.mainface;



import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

	private MainFaceView mGLSurFaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
          
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
	              WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		setContentView(R.layout.activity_main);	

		mGLSurFaceView = new MainFaceView(this);
		mGLSurFaceView.requestFocus();
		RelativeLayout ll = (RelativeLayout) findViewById(R.id.main_face);
		ll.addView(mGLSurFaceView);

    }
    

    
    @Override
    protected void onResume() {
        super.onResume();
        mGLSurFaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurFaceView.onPause();
    }   
}
