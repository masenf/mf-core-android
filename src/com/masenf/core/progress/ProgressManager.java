package com.masenf.core.progress;

import java.util.Collection;
import java.util.HashMap;

import com.masenf.core.async.callbacks.BaseCallback;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Manages the life cycle of ProgressItems across long operations, and
 * state changes in the application. Should be initialized with the current
 * activity context. When the layout changes or is rebuilt, the ProgressFragment
 * reference should be updated to point to the visible, instantiated fragment,
 * to allow for propagation of creation and expire events.
 * @author masenf
 */
public class ProgressManager {

	private static final String TAG = "ProgressManager";
	private static final int expire_delay_msec = 340;
	private HashMap<String,ProgressItem> ptags = new HashMap<String,ProgressItem>();
	private BaseCallback completeCallback = null;
	private ProgressFragment pf;
	private static ProgressManager ins;
	
	private ProgressManager(Context ctx) {
		// TODO: don't regenerate this each time?
		completeCallback = new BaseCallback() {
			@Override
			public void notifyComplete(boolean success, final String tag) {
				final Runnable expire = new Runnable() {
					@Override
					public void run() {
						expireProgress(tag);
					}
					
				};
				final Handler h = new Handler();
				if (success) {
					h.postDelayed(expire, expire_delay_msec);
				} else {
					ProgressItem p = ptags.get(tag);
					p.setLabel("Error encounter. Tap to dismiss");
					p.getView().setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							h.post(expire);
						}
					});
				}
			}
		};
	}
	
	/**
	 * initialize the global ProgressManager with the activity context. also creates
	 * a new completion callback (this may be a bug)
	 * @param ctx the current activity context
	 */
	public static ProgressManager initManager(Context ctx) {
		// to be called from the activity 
		if (ins == null)
			ins = new ProgressManager(ctx);
		return ins;
	}
	/**
	 * This method may return a null pointer if the global ProgressManager has not
	 * yet been initialized
	 * @return an instance of the global ProgressManager
	 */
	public static ProgressManager getInstance() {
		// returns null if not initialized
		return ins;
	}
	/**
	 * Generally called from the ProgressFragment to initially populate its list of 
	 * progress bars
	 * @return A collection of all ProgressItems managed by this ProgressManager
	 */
	public Collection<ProgressItem> getAllItems() {
		return ptags.values();
	}
	/**
	 * 
	 * @param tag the unique id of the ProgressItem
	 * @return the ProgressCallback associated with tag
	 */
	public ProgressCallback getProgressCallback(String tag) {
		if (ptags.containsKey(tag)) {
			return ptags.get(tag);
		}
		return null;
	}
	/**
	 * Generate a new ProgressItem identified by tag to be managed by this ProgressManager. 
	 * Also causes the ProgressItem to be displayed on the active ProgressFragment, if set.
	 * @param tag the unique identifier of the task being monitored
	 * @return ProgressCallback that can be used for updating the progress
	 */
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
	/**
	 * Stop monitoring and displaying the ProgressItem associated with tag. Also 
	 * calls through to the active ProgressFragment if set
	 * @param tag the unique identifier of the task being expired
	 */
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
	/**
	 * Reset the active ProgressFragment. Should be called any time the ProgressFragment 
	 * gets recreated, even if restored.
	 * @param pf the active ProgressFragment
	 */
	public void setProgressFragment(ProgressFragment pf)
	{
		Log.v(TAG,"setProgressFragment(" + pf + ")");
		this.pf = pf;
	}
}
