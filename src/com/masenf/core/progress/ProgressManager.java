package com.masenf.core.progress;

import java.util.Collection;
import java.util.HashMap;

import com.masenf.core.async.callbacks.BaseCallback;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class ProgressManager {
	private static final String TAG = "ProgressManager";
	private HashMap<String,ProgressItem> ptags = new HashMap<String,ProgressItem>();
	private BaseCallback completeCallback = null;
	private ProgressFragment pf;
	private static ProgressManager ins;
	private ProgressManager(Context ctx) {
		completeCallback = new BaseCallback() {
			@Override
			public void notifyComplete(boolean success, final String tag) {
				Handler h = new Handler();
				h.postDelayed(new Runnable() {
					@Override
					public void run() {
						expireProgress(tag);
					}
					
				}, 2000);
			}
		};
	}
	public static ProgressManager initManager(Context ctx) {
		// to be called from the activity 
		if (ins == null)
			ins = new ProgressManager(ctx);
		return ins;
	}
	public static ProgressManager getInstance() {
		// returns null if not initialized
		return ins;
	}
	public Collection<ProgressItem> getAllItems() {
		return ptags.values();
	}
	public ProgressCallback getProgressCallback(String tag) {
		if (ptags.containsKey(tag)) {
			return ptags.get(tag);
		}
		return null;
	}
	public ProgressCallback createProgressCallback(String tag) {
		Log.v(TAG,"createProgressCallback() for " + tag);
		ProgressItem p = new ProgressItem();
		p.setCallback(completeCallback);
		if (tag != null)
			p.setTag(tag);
		else
			tag = p.getTag();
		ptags.put(tag, p);
		if (pf != null)
			pf.onCreateItem(p);
		return p;
	}
	public void expireProgress(String tag) {
		// wipe out a progress tag, remove all refs
		if (ptags.containsKey(tag)) {
			Log.v(TAG,"expireProgress() " + tag + " has completed, removing it from list");
			ProgressItem p = ptags.get(tag);
			if (pf != null)
				pf.onExpireItem(p);
			ptags.remove(tag);
			p = null;
		}
	}
	public void setProgressFragment(ProgressFragment pf)
	{
		Log.v(TAG,"setProgressFragment(" + pf + ")");
		this.pf = pf;
	}
}
