package com.masenf.core.async;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

/**
 * Make an HTTP Request in the background.<br><br>
 * Subclasses must call the protected methods here to do anything useful with the class.
 * @author masenf
 *
 * @param <Params> usually will be URL or something to generate a URL from
 * @param <Result> the object returned by the Task
 */
public abstract class HTTPRequestTask<Params, Result> extends ProgressReportingTask<Params, Result> {

	private static final String TAG = "HTTPRequestTask";
	protected static int BufferSz = 1024;		// 1kb buffer
	protected HttpURLConnection urlConnection;

	protected BufferedInputStream makeRequest(URL url) {
		Log.v(TAG, "makeRequest() beginning async fetch of " + url.toString());
		BufferedInputStream bis = null;
		try {
			postStatus("Connecting...");
			urlConnection = (HttpURLConnection) url.openConnection();
			postStatus("Connection established, awaiting reply");
		} catch (IOException e1) {
			appendError("IOException creating connection: " + e1.getMessage());
			return bis;
		}
		try {
			postStatus("Fetching data...");
			if (urlConnection.getResponseCode() == 200)
			{
				Log.v(TAG, "makeRequest() request successful, data is " + urlConnection.getContentLength() + " bytes");

				postProgressMax(urlConnection.getContentLength());
				InputStream is = urlConnection.getInputStream();
				bis = new BufferedInputStream(is);
			} else {
				Log.v(TAG, "makeRequest() server returned non-200: " + urlConnection.getResponseCode() + " " + urlConnection.getResponseMessage());;
			}
		} catch (IOException e) {
			appendError("IOException reading response: " + e.toString());
			Log.v(TAG, "makeRequest() unsuccessful connection " + e.toString());
		}
		return bis;
	}
	protected String readToString(BufferedInputStream bis) {
		if (bis == null) {
			Log.w(TAG,"readToString() InputStream is null");
			return "";
		}
		StringBuilder res = new StringBuilder();	// the response
		byte[] raw = new byte[BufferSz];
		int total_bytes = 0;
		int bytes_read = 0;
		try {		
			while (bytes_read > -1)
			{
				total_bytes += bytes_read;
				bytes_read = bis.read(raw);
				for (int i=0;i<bytes_read;i++)
					res.append((char) raw[i]);
				postProgress(total_bytes);
			}
			postStatus("OK...received " + total_bytes + " bytes");
		} catch (IOException e) {
			appendError("IOException reading response: " + e.toString());
		} finally {
			urlConnection.disconnect();		// close the connection
		}
		return res.toString();
	}
	protected File readToFile(BufferedInputStream bis, File out) {
		if (bis == null) {
			Log.w(TAG,"readToFile() InputStream is null");
			return null;
		}
		OutputStream bos;
		try {
			bos = new BufferedOutputStream(new FileOutputStream(out));
		} catch (FileNotFoundException e1) {
			Log.w(TAG,"readToFile() Error opening file " + out.toString() + " for writing");
			return null;
		}
		byte[] raw = new byte[BufferSz];
		int total_bytes = 0;
		int bytes_read = 0;
		try {		
			do {
				total_bytes += bytes_read;
				postProgress(total_bytes);
				bos.write(raw, 0, bytes_read);
				
				bytes_read = bis.read(raw);
			} while (bytes_read > -1);
			bos.close();
			bis.close();
			Log.v(TAG,"Downloaded " + total_bytes + " bytes");
			postStatus("OK...received " + total_bytes + " bytes");
		} catch (IOException e) {
			appendError("IOException reading response: " + e.toString());
		} finally {
			urlConnection.disconnect();		// close the connection
		}
		return out;
	}
}
