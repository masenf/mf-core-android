package com.masenf.core;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.masenf.core.async.HTTPRequestTask;
import com.masenf.core.async.JSONRequestTask;
import com.masenf.core.async.callbacks.RequestCallback;

/**
 * <ol>
 * <li>Query for latest version</li>
 * <li>Download latest version</li>
 * <li>Launch Installer on downloaded APK</li>
 * </ol>
 * 
 * <p>Query a url for a JSON object containing at least versionCode
 * and apk keys. The returned object should specify the latest
 * versionCode offered by the server and a fully qualified download 
 * link for the APK.</p>
 * 
 * <p>The downloadUpdate method will automatically invoke the installUpdate 
 * method upon success</p>
 * 
 * @author masenf
 *
 */
public abstract class ApplicationUpdater {

	private static final String TAG = "ApplicationUpdater";
	private int currentVersionCode;
	private String newVersionCodeURL;
	private Context ctx;
	
	private int latestVersionCode;
	private URL apkURL;
	private File downloadLoc;
	private boolean downloaded = false;
	
	/**
	 * Construct a new ApplicationUpdater
	 * 
	 * @param newVersionCodeURL the URL pointer to the
	 * JSON-object-returning GET service. Accessing this URL should
	 * return info on the latest version known by the update server.
	 * @param ctx the parent Activity
	 */
	public ApplicationUpdater(String newVersionCodeURL, Context ctx)
	{
		this.newVersionCodeURL = newVersionCodeURL;
		this.ctx = ctx;
		try {
			PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
			currentVersionCode = pInfo.versionCode;
		} catch (NameNotFoundException e) {
			Toast.makeText(ctx, "Could not find application in Package Directory", Toast.LENGTH_SHORT).show();
			Log.v(TAG,e.toString());
			currentVersionCode = 0;
		}
	}
	/**
	 * call after fetchUpdateData()
	 * @return the latestVersionCode reported by the server
	 */
	public int getLatestVersionCode() {
		return latestVersionCode;
	}
	/**
	 * call after fetchUpdateData()
	 * @return the URL to the latest version reported by the server
	 */
	public URL getApkURL() {
		return apkURL;
	}
	/**
	 * @return true if the download was successful, otherwise false
	 */
	public boolean downloadSuccess()
	{
		return downloaded;
	}
	/**
	 * @return true if the latest version found is greater than the current version
	 */
	public boolean updateAvailable()
	{
		return latestVersionCode > currentVersionCode;
	}
	/**
	 * hook called after updated data is downloaded and parsed (if successful)
	 */
	public void postFetch() { };
	/**
	 * hook called after APK is downloaded (if successful)
	 */
	public void postDownload() { };

	/**
	 * spawn a background task to fetch the update data from the URL passed 
	 * in during object construction. Called postFetch upon successful completion.
	 */
	public void fetchUpdateData()
	{
		Log.d(TAG,"updateAvailable() - checking " + newVersionCodeURL + " for available updates");
    	try {
			URL u = new URL(newVersionCodeURL);
			new JSONRequestTask(new RequestCallback<JSONObject>() {
				@Override
				public void updateData(JSONObject result) {
					Log.d(TAG,"updateData() - Received JSONObject from thread");
					String raw_apkURL = "";
					try {
						latestVersionCode = result.getInt("versionCode");
						raw_apkURL = result.getString("apk");
						apkURL = new URL(raw_apkURL);
						postFetch();
					} catch (JSONException e) {
						latestVersionCode = -1;		// error
						Log.v(TAG,"Incorrect response from server. Expecting an object with " +
								"versionCode and apk properties. Got: " + result.toString());
					} catch (MalformedURLException e) {
						latestVersionCode = -1;
						Log.v(TAG,"Resulting URL could not be parsed: " + raw_apkURL);
					}
				}
			}).executeOnExecutor(JSONRequestTask.THREAD_POOL_EXECUTOR, u);
		} catch (MalformedURLException e) {
			Log.e(TAG,"Malformed url: " + newVersionCodeURL);
		}
	}

	/**
	 * <p>Begin downloading the APK specified by the update server. If this method
	 * is used before fetching update data, or if the fetch failed, returns immediately.
	 * 
	 * <p>Sets downloaded to true and calls postDownload() on success
	 */
	public void downloadUpdate()
	{
		if (apkURL == null)
			return;
		String rootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
		downloadLoc = new File(rootPath + File.separator + "update.apk");
		Log.v(TAG,"downloadAndInstallUpdate() - downloading to " + downloadLoc);
		new HTTPRequestTask<URL,File>() {
			@Override
			protected File doInBackground(URL... params) {
				return readToFile(makeRequest(params[0]), downloadLoc);
			}
			@Override
			protected void onPostExecute(File result) {
				if (result != null && result.canRead()) {
					downloaded = true;
					postDownload();
				} else {
					Toast.makeText(ctx,"Download failed, please try again later", Toast.LENGTH_LONG).show();
				}
				super.onPostExecute(result);
			}
		}.executeOnExecutor(HTTPRequestTask.THREAD_POOL_EXECUTOR, apkURL);
	}
	/**
	 * launch an Intent to ask the user to update the application with the
	 * APK file downloaded previously. If the download was unsuccessful, this
	 * method returns immediately. There is no notification after install, the
	 * activity will exit.
	 */
	public void installUpdate()
	{
		if (!downloaded) {
			Log.e(TAG,"Download failed, nothing to install");
			return;
		}
		Uri u = Uri.fromFile(downloadLoc);
		Log.v(TAG,"Attempting to install " + u.toString());
		Intent promptInstall = new Intent(Intent.ACTION_VIEW)
	    .setDataAndType(u, "application/vnd.android.package-archive")
		.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		ctx.startActivity(promptInstall); 
	}
}
