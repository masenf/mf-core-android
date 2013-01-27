package com.masenf.core.progress;

import com.masenf.core.async.callbacks.BaseCallback;

public class ProgressCallback extends BaseCallback {
	// basic progress reporting
	public void startProgress(Integer max) { };		// set to null for indeterminate
	public void stopProgress() { };
	public void updateError(String msg) { updateStatus(msg); };
	public void updateStatus(String msg) { };
	
	// detailed updates
	public void onProgress(ProgressUpdate update) { };
}
