package com.masenf.core.progress;

import com.masenf.core.R;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;

public class ProgressActivity extends Activity {

	private static final String TAG = "ProgressActivity";
	private ProgressManager pm;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.progress_activity);
		
		Log.v(TAG,"onCreate() - attempting to initialize ProgressManager");
		pm = ProgressManager.initManager(this);
	}
	@Override
	public void onStart() {
		super.onStart();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment GlobalProgress = fm.findFragmentByTag("GlobalProgress");
		if (GlobalProgress == null) {
			Log.v(TAG,"onStart() - creating Fragment tagged GlobalProgress");
			GlobalProgress = new ProgressFragment();
			ft.add(R.id.progress_fragment_placeholder, GlobalProgress, "GlobalProgress");
		}
		ft.commit();
		pm.setProgressFragment((ProgressFragment) GlobalProgress);
	}
	@Override
	public void onStop() {
		pm.setProgressFragment(null);
		super.onStop();
	}
}
