package com.masenf.core;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * This AnimationListener does nothing by default. Allows for cleaner 
 * in-place extension when one only wants to override one hook.
 * @author masenf
 *
 */
public class NullAnimationProto implements AnimationListener {

	@Override
	public void onAnimationEnd(Animation animation) {
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	@Override
	public void onAnimationStart(Animation animation) {
	}

}
