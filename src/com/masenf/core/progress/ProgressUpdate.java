package com.masenf.core.progress;

/**
 * Encapsulates the things you'd want to update in a progress item. Update
 * as needed, but maintain the interface and argument order
 * TODO: create an interface for this thing
 * @author masenf
 *
 */
public class ProgressUpdate {

	/**
	 * The maximum value of the progress
	 */
	public Integer max 		= null;
	/**
	 * The new value of the progress
	 */
	public Integer sofar	= null;
	/**
	 * The new error message
	 */
	public String  error 	= null;
	/**
	 * The new status message
	 */
	public String  status   = null;
	/** 
	 * The new progress label
	 */
	public String  label	= null;
	
	public ProgressUpdate(Integer sofar, Integer max, String error) {
		this(sofar, max, error, null, null);
	}
	public ProgressUpdate(Integer sofar, Integer max) {
		this(sofar, max, null);
	}
	public ProgressUpdate(Integer sofar) {
		this(sofar, null, null);
	}
	public ProgressUpdate() {
	}
	private ProgressUpdate(Integer sofar, Integer max, String error, String status, String label) {
		this.sofar = sofar;
		this.max = max;
		this.error = error;
		this.status = status;
		this.label = label;
	}
	/**
	 * @param msg the new status message
	 * @return a ProgressUpdate with only the status set
	 */
	public static ProgressUpdate status (String msg) {
		return new ProgressUpdate(null,null,null,msg,null);
	}
	/**
	 * @param msg the new error message
	 * @return a ProgressUpdate with only the error message set
	 */
	public static ProgressUpdate error (String msg) {
		return new ProgressUpdate(null,null,msg,null,null);
	}
	/**
	 * @param msg the new label
	 * @return a ProgressUpdate with only the label set
	 */
	public static ProgressUpdate label (String msg) {
		return new ProgressUpdate(null,null,null,null,msg);
	}
}
