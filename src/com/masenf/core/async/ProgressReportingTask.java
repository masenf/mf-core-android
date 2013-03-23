package com.masenf.core.async;

import java.util.UUID;

import android.os.AsyncTask;
import android.util.Log;

import com.masenf.core.progress.ProgressCallback;
import com.masenf.core.progress.ProgressManager;
import com.masenf.core.progress.ProgressUpdate;

/**
 * An AsyncTask which handles updating a ProgressItem. Tasks extending 
 * ProgressReportingTask can update their ProgressItem by calling the
 * protected post* methods 
 * @author masenf
 *
 * @param <Params> 
 * @param <Result>
 */
public abstract class ProgressReportingTask<Params, Result> extends AsyncTask<Params, ProgressUpdate, Result> {

	private static final String TAG = "ProgressReportingTask";
	protected ProgressManager pg = null;
	private String taskid = "Default";
	private String error = "";
	
	private ProgressCallback getProgressCallback() {
		// this stays private to prevent stupid errors like updating the progress from the bg thread
		// use publishProgress(ProgressUpdate) to update the UI safely
		if (pg != null) {
			return pg.getProgressCallback(taskid);
		} else {
			Log.w(TAG,"getProgressCallback() can't get callback because ProgressManager is null");
		}
		return new ProgressCallback();
	}
	protected void postError(String msg) {
		publishProgress(ProgressUpdate.error(msg));
	}
	protected void postStatus(String msg) {
		publishProgress(ProgressUpdate.status(msg));
	}
	protected void postLabel(String label) {
		publishProgress(ProgressUpdate.label(label));
	}
	protected void appendError(String msg) {
		error += msg;
		postError(error);
	}
	protected boolean hasError() {
		if (error == "") 
			return false;
		return true;
	}
	protected String getError() {
		return error;
	}
	protected void postProgressMax(Integer max) {
		publishProgress(new ProgressUpdate(0,max));
	}
	protected void postProgress(Integer sofar) {
		publishProgress(new ProgressUpdate(sofar));
	}
	
	@Override
	protected void onPreExecute() {
		Log.v(TAG,"onPreExecute() for " + getClass().getName());
		pg = ProgressManager.getInstance();
		taskid = UUID.randomUUID().toString();		// generate a nice random tag for this task
		if (pg != null) {
			ProgressCallback p = pg.createProgressCallback(taskid);
			p.startProgress(null);
			p.onProgress(ProgressUpdate.label(getClass().getName()));
		} else {
			Log.w(TAG,"onPreExecute() can't update progress because ProgressManager is null");
		}
	}
	
	@Override 
	protected void onProgressUpdate(ProgressUpdate... progress) {
		int count = progress.length;
		for (int i = 0; i< count; i++) {
			getProgressCallback().onProgress(progress[i]);
		}
	}
	
	@Override
	protected void onPostExecute(Result result) {
		if (pg != null) {
			ProgressCallback cb = getProgressCallback();
			cb.stopProgress();
			if (hasError()) {
				cb.updateError(error);
				cb.notifyComplete(false, taskid);
			}
			else {
				cb.notifyComplete(true, taskid);
			}
		}
		Log.v(TAG,"onPostExecute() for " + getClass().getName());
	}

}
