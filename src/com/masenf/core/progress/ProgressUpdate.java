package com.masenf.core.progress;

public class ProgressUpdate {
	// encapsulates the things that you'd want to update
	public Integer max 		= null;
	public Integer sofar	= null;
	public String  error 	= null;
	public String  status   = null;
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
	public static ProgressUpdate status (String msg) {
		return new ProgressUpdate(null,null,null,msg,null);
	}
	public static ProgressUpdate error (String msg) {
		return new ProgressUpdate(null,null,msg,null,null);
	}
	public static ProgressUpdate label (String msg) {
		return new ProgressUpdate(null,null,null,null,msg);
	}
}
