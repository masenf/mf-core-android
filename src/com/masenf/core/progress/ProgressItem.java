package com.masenf.core.progress;

import com.masenf.core.DrawingItem;
import com.masenf.core.async.callbacks.BaseCallback;
import com.masenf.wtaandroid.R;
import com.masenf.wtaandroid.R.id;
import com.masenf.wtaandroid.R.layout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressItem extends ProgressCallback implements DrawingItem {

	private static final String TAG = "ProgressItem";
	
	private String tag;
	private ProgressBar progress;
	private TextView txt_error;
	private TextView txt_lbl;
	private View thisView;
	private boolean inprogress = false;
	private int progress_sofar = 0;
	private int progress_max = 0;
	private String error_message = "";
	private boolean iscomplete = false;
	private String label = "";
	
	private BaseCallback cb = null;		// used to notify complete
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getTag() { 
		return tag;
	}
	public void setCallback(BaseCallback cb) {
		this.cb = cb;
	}
	public boolean isInProgress() {
		return inprogress;
	}
	public boolean isComplete() {
		return iscomplete;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		if (label == null)
			return;
		if (txt_lbl != null)
			txt_lbl.setText(label);
		this.label = label;
	}
	public int getProgress() {
		return progress_sofar;
	}
	public void setProgress(Integer sofar) {
		if (sofar != null) {
			if (!inprogress)
				startProgress(progress_max);
			progress_sofar = sofar;
			if (progress != null)
				progress.setProgress(sofar);
		}
	}
	public int getProgressMax() {
		return progress_max;
	}
	public String getErrorMessage() {
		return error_message;
	}
	
	public ProgressItem() {
	}
	public ProgressItem(Bundle state) {
		restoreItemState(state);
	}
	
	public void hideError() {
		Log.d(TAG,"hideError() - hiding error message");
		error_message = "";
		if (txt_error != null)
			txt_error.setText("");
	}
	@Override
    public void startProgress(Integer max) {
		//Log.v(TAG, "startProgress() - initializing progress_flat");
		if (inprogress && max == null)
			return;
		// show the view
		if (thisView != null) {
			thisView.setVisibility(View.VISIBLE);
		}
		
		// update the view if it is created
		if (progress != null) {
			progress.setEnabled(true);
			progress.setProgress(0);
	    	if (max != null && max > 1) {
		    		progress.setIndeterminate(false);
		    		progress.setMax(max);
	    	} else {
	    		progress.setIndeterminate(true);
	    	}
		} else {
			Log.i(TAG,"startProgress() - view not created yet, postponing start");
		}
		
		// set persistent values
		inprogress = true;
		progress_sofar = 0;
		if (max != null)
			progress_max = max;
    }
	@Override
	public void onProgress(ProgressUpdate u) {
		// unpack the update
		Log.v(TAG,"onProgress() posted status update for " + tag);
		if (u != null) {
			startProgress(u.max);
			setProgress(u.sofar);
			updateError(u.error);
			setLabel(u.label);
		}
	}
	@Override
	public void updateError(String msg) {
		//Log.v(TAG, "updateError() - setting error message to '" + msg + "'");
		if (msg == null) {
			return;
		}
		error_message = msg;
		if (txt_error != null) {
			txt_error.setText(msg);
		} else {
			Log.i(TAG,"updateError() - view not created yet, postponing message");
		}
	}
	@Override
	public void notifyComplete(boolean success, String tag) {
		super.notifyComplete(success, tag);
		if (success)
			hideError();
		iscomplete = true;
		if (cb != null)
			cb.notifyComplete(success, getTag());
	}
	@Override
	public int getViewLayout() {
		return R.layout.progress_item;
	}
	@Override
	public View updateView(View convertView) {
		if (tag != null)
			Log.v(TAG,"updateView() building view for " + tag);
		// store refs
		thisView = convertView;
		progress = (ProgressBar) convertView.findViewById(R.id.progress_flat);
		txt_error = (TextView) convertView.findViewById(R.id.txt_error);
		txt_lbl = (TextView) convertView.findViewById(R.id.txt_lbl);
		
		// update views
		if (tag == null) {
			// an uninitialized item
			convertView.setVisibility(View.GONE);
		} else {
			if (inprogress) {
				startProgress(progress_max);
				setProgress(progress_sofar);
			}
			updateError(error_message);
		}
		setLabel(label);
		convertView.setTag(this);
		return convertView;
	}
	public Bundle saveItemState() {
		Bundle s = new Bundle();
		s.putBoolean("inprogress", inprogress);
		s.putBoolean("iscomplete", iscomplete);
		s.putInt("progress_sofar", progress_sofar);
		s.putInt("progress_max", progress_max);
		s.putString("error_message", error_message);
		s.putString("tag", tag);
		return s;
	}
	public void restoreItemState(Bundle s) {
		if (s != null) {
			if (s.getBoolean("inprogress", false)) {
				startProgress(s.getInt("progress_max",0));
				setProgress(s.getInt("progress_sofar",0));
			}
			updateError(s.getString("error_message", ""));

			iscomplete = s.getBoolean("iscomplete",false);
			tag = s.getString("tag",null);
		}
	}
}
