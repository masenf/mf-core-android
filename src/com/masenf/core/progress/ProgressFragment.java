package com.masenf.core.progress;

import java.util.Collection;
import com.masenf.core.R;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class ProgressFragment extends Fragment {
	private static final String TAG = "ProgressFragment";
	private LinearLayout ll;
	private int cindex = 0;

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
	public void onCreateItem(ProgressItem p) {
		Log.v(TAG,"onCreateItem() - inflating new progress_item.xml");
		View v = getActivity().getLayoutInflater().inflate(R.layout.progress_item, ll, false);
		p.setView(v);
		ll.addView(v, cindex);
		ll.requestLayout();
		cindex++;
	}
	public void onExpireItem(ProgressItem p) {
		Log.v(TAG,"onExpireItem() - removing " + p.getTag() + " from the view");
		ll.removeView(p.getView());
		p.setView(null);
		ll.requestLayout();
		cindex--;
	}
}
