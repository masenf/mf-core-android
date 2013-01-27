package com.masenf.core.progress;

import java.util.Collection;
import java.util.concurrent.ArrayBlockingQueue;

import com.masenf.core.R;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;

public class ProgressFragment extends Fragment {
	private static final String TAG = "ProgressFragment";
	private ArrayBlockingQueue<View> toDestroy = new ArrayBlockingQueue<View>(32);
	private LinearLayout ll;
	private int cindex = 0;
	private Handler h = new Handler();
	private boolean pruning = false;
	
	private static final int display_delay_msec = 250;		// wait this long before displaying a progress item
	private static final int fade_time_msec = 250;
	private static final int prune_delay_msec = 400;

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		Log.v(TAG, "onCreateView() - inflating layout");
		// Inflate the layout for this fragment
		ll = new LinearLayout(getActivity());
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		Collection<ProgressItem> items = ProgressManager.getInstance().getAllItems();
		for (ProgressItem p : items) {
			onCreateItem(p);
		}
    	return ll;
    }
	@Override
	public void onDestroyView() {
		// nullify all the views
		Collection<ProgressItem> items = ProgressManager.getInstance().getAllItems();
		for (ProgressItem p : items) {
			p.setView(null);
		}
		super.onDestroyView();
	}
	public void onCreateItem(final ProgressItem p) {
		// delay the creation of the view to prevent jittering
		h.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (!p.isComplete() || p.hasError()) {	// only create the view if still in progress or is showing an error
					Log.v(TAG,"onCreateItem() - inflating new progress_item.xml");
					View v = getActivity().getLayoutInflater().inflate(R.layout.progress_item, ll, false);
					p.setView(v);
					ll.addView(v, cindex);
					ll.requestLayout();
					final Animation in = new AlphaAnimation(0.0f,1.0f);
					in.setDuration(fade_time_msec);
					v.startAnimation(in);
					cindex++;
				}
			}
		}, display_delay_msec);
	}
	public void onExpireItem(final ProgressItem p) {
		Log.v(TAG,"onExpireItem() - removing " + p.getTag() + " from the view");
		final View v = p.getView();
		if (v == null)
			return;
		final Animation out = new AlphaAnimation(1.0f, 0.0f);
		out.setDuration(fade_time_msec);
		out.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				v.setVisibility(View.GONE);
				toDestroy.add(v);
				p.setView(null);
			}
			@Override
			public void onAnimationRepeat(Animation arg0) {}
			@Override
			public void onAnimationStart(Animation arg0) {}
		});
		v.startAnimation(out);
		prune();
	}
	public void prune() 
	{
		// remove dead views from the layout in a safe way. multiple
		// quick tasks will only cause the view to be recalculated
		// fewer times
		if (toDestroy.size() > 0) {
			Log.v(TAG,"Pruning " + toDestroy.size() + " destroyed views");
			View d;
			while ((d = toDestroy.poll()) != null) {
				ll.removeView(d);
				cindex--;
			}
			ll.requestLayout();
			pruning = false;
		} else {		// no views ready to be destroyed
			if (!pruning) {
				// we're not already waiting, so wait a bit and try again
				h.postDelayed(new Runnable() {
					@Override
					public void run() {
						prune();
					}
				}, prune_delay_msec);
				pruning = true;
			}
		}
	}
}
