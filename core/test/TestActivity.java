package com.masenf.core.test;

import com.masenf.wtaandroid.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.masenf.core.TabNavActivity;
import com.masenf.core.async.JSONRequestTask;
import com.masenf.core.async.ProgressReportingTask;
import com.masenf.core.progress.ProgressUpdate;

public class TestActivity extends TabNavActivity {
	
	private static final String TAG = "TestActivity";
	
	private class SleepTask extends ProgressReportingTask<Integer,Integer> {
		@Override
		protected Integer doInBackground(Integer... arg0) {
			Log.i(TAG,"Starting Sleep Task");
			postLabel("Sleep task: " + arg0[0] + "ms");
			int max = arg0[0] / num_updates;
			postProgressMax(max);
			try {
				for(int i=0;i<max;i++) {
					Thread.sleep(max);
					publishProgress(new ProgressUpdate(i,max));
				}
			} catch (InterruptedException e) {
				// we blew up, drat
			}
			return 0;
		}
		@Override
		protected void onPostExecute(Integer result) {
			postError("I'm Awake!");
			super.onPostExecute(result);
		}
	}
	
	private static int num_updates = 200;	// we'll update the progress 200 times
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG,"onCreate() setting button callbacks");
		setContentView(R.layout.test_activity);
		LinearLayout button_container = (LinearLayout) findViewById(R.id.button_container);
		int buttons = button_container.getChildCount();
		for (int i=0;i<buttons;i++) {
			Button b = (Button) button_container.getChildAt(i);
			b.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					SleepTask st = new SleepTask();
					Log.i(TAG,"Spawning new SleepTask");
					st.executeOnExecutor(SleepTask.THREAD_POOL_EXECUTOR, (Integer.parseInt((String) v.getTag())));
				}	
			});
		}
	}
}
