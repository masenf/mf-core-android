package com.masenf.core.progress;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.masenf.core.R;
import com.masenf.core.async.callbacks.BaseCallback;

/**
 * an encapsulation of a Progress tracking and updating item which creates and renders
 * views for display in a ProgressFragment. Uses asset layout/progress_item.xml
 * @author masenf
 *
 */
public class ProgressItem extends ProgressCallback {

	private static final String TAG = "ProgressItem";
	
	private String tag;
	private ProgressBar progress;
	private TextView txt_error;
	private TextView txt_lbl;
	private View thisView;
	private boolean inprogress = false;
	private int progress_sofar = 0;
	private int progress_max = 0;
	private String status_message = "";
	private boolean haserror = false;
	private boolean iscomplete = false;
	private String label = "";
	
	private BaseCallback cb = null;		// used to notify complete
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getTag() { 
		return tag;
	}
	public boolean hasError() {
		return haserror;
	}
	public View getView() {
		return thisView;
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
			if (progress != null) {
				progress.setProgress(sofar);
			}
		} else {
			Log.i(TAG,"setProgress() - view not created yet, postponing update");
		}
	}
	public int getProgressMax() {
		return progress_max;
	}
	public String getStatusMessage() {
		return status_message;
	}
	
	public ProgressItem() {
	}
	public ProgressItem(Bundle state) {
		restoreItemState(state);
	}
	
	public void hideStatus() {
		Log.d(TAG,"hideStatus() - hiding status message");
		status_message = "";
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
		if (max != null)
			progress_max = max;
    }
	@Override
	public void onProgress(ProgressUpdate u) {
		// unpack the update
		if (u != null) {
			Log.v(TAG,"onProgress() posted status update for " + tag);
			startProgress(u.max);
			setProgress(u.sofar);
			updateStatus(u.status);
			updateError(u.error);
			setLabel(u.label);
		} else {
			Log.w(TAG,"onProgress() posted a NULL status update for " + tag);
		}
	}
	@Override
	public void updateError(String msg) {
		if (msg == null)
			return;
		haserror = true;
		updateStatus(msg);
	}
	@Override
	public void updateStatus(String msg) {
		//Log.v(TAG, "updateError() - setting error message to '" + msg + "'");
		if (msg == null) {
			return;
		}
		status_message = msg;
		if (txt_error != null) {
			txt_error.setText(msg);
		} else {
			Log.i(TAG,"updateStatus() - view not created yet, postponing message");
		}
	}
	@Override
	public void notifyComplete(boolean success, String tag) {
		super.notifyComplete(success, tag);
		if (!success)
			haserror = true;
		iscomplete = true;
		setProgress(progress_max);		// make sure the bar is all the way over
		if (cb != null)
			cb.notifyComplete(success, getTag());
	}
	/**
	 * sets the view for this item to an inflated view derived from layout/progress_item.xml
	 * @param myView if null, disables display of this ProgressItem
	 */
	public void setView(View myView) {
		thisView = myView;
		if (thisView == null) {
			progress = null;
			txt_error = null;
			txt_lbl = null;
			return;
		}
		if (tag != null)
			Log.v(TAG,"updateView() building view for " + tag + ". View id = " + myView.toString());
		// store refs
		progress = (ProgressBar) thisView.findViewById(R.id.progress_flat);
		txt_error = (TextView) thisView.findViewById(R.id.txt_error);
		txt_lbl = (TextView) thisView.findViewById(R.id.txt_lbl);
		// update views
		if (tag != null) {
			if (inprogress) {
				startProgress(progress_max);
				setProgress(progress_sofar);
			}
			updateStatus(status_message);
		}
		setLabel(label);
		myView.setTag(this);
	}
	public Bundle saveItemState() {
		Bundle s = new Bundle();
		s.putBoolean("inprogress", inprogress);
		s.putBoolean("iscomplete", iscomplete);
		s.putBoolean("haserror", haserror);
		s.putInt("progress_sofar", progress_sofar);
		s.putInt("progress_max", progress_max);
		s.putString("status_message", status_message);
		s.putString("tag", tag);
		return s;
	}
	public void restoreItemState(Bundle s) {
		if (s != null) {
			inprogress = s.getBoolean("inprogress", false);
			progress_max = s.getInt("progress_max",0);
			progress_sofar = s.getInt("progress_sofar",0);
			status_message = s.getString("status_message");
			iscomplete = s.getBoolean("iscomplete",false);
			haserror = s.getBoolean("haserror",false);
			tag = s.getString("tag");
		
			// update the UI if it is drawn
			if (thisView != null) {
				setView(thisView);
			}
		}
	}
}
