package com.masenf.core.async.callbacks;

/**
 * Callback passed to AsyncTasks to notify using code of success or failure
 * @author masenf
 *
 */
public class BaseCallback {
	public void notifyComplete(boolean success, String tag) { };
	public void notifyException(Exception ex) { };
}
