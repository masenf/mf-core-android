package com.masenf.core.progress;

import com.masenf.core.async.callbacks.BaseCallback;

/**
 * Callback for the purposes of updating progress from other threads or
 * AsyncTasks. Typically extended
 * @author masenf
 *
 */
public class ProgressCallback extends BaseCallback {
	// basic progress reporting
	
	/**
	 * reset the progress counter. Set max to null for an
	 * indeterminate status
	 * @param max the maximum value of the progress
	 */
	public void startProgress(Integer max) { };
	/**
	 * set the progress to complete and stop updating
	 */
	public void stopProgress() { };
	/**
	 * set the Error string for this progress
	 * @param msg the text to be displayed
	 */
	public void updateError(String msg) { updateStatus(msg); };
	/**
	 * set a generic status message for this progress
	 * @param msg the text to be displayed
	 */
	public void updateStatus(String msg) { };
	
	// detailed updates
	/**
	 * pass a detailed update
	 * @param update a ProgressUpdate data class
	 */
	public void onProgress(ProgressUpdate update) { };
}
