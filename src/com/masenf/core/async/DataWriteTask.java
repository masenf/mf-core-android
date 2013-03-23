package com.masenf.core.async;

import java.util.ArrayList;

import android.database.sqlite.SQLiteException;

import com.masenf.core.async.callbacks.DataWriteCallback;
import com.masenf.core.data.DataUpdateQuery;

public class DataWriteTask extends ProgressReportingTask<DataUpdateQuery, ArrayList<Long>> {

	private DataWriteCallback cb;
	public DataWriteTask(DataWriteCallback cb) {
		this.cb = cb;
	}
	@Override
	protected ArrayList<Long> doInBackground(DataUpdateQuery... params) {
		ArrayList<Long> result = new ArrayList<Long>();
		postProgressMax(params.length);
		for (int i=0;i<params.length;i++)
		{
			DataUpdateQuery q = params[i];
			ArrayList<Long> intermediate = null;
			try {
				intermediate = q.execute();
			} catch (SQLiteException ex) {
				appendError("Database error: " + ex.getMessage());
			}
			if (intermediate != null)
				result.addAll(intermediate);
			if (params.length > 1)
				postProgress(i);
		}
		postStatus("Posted " + result.size() + " records");
		return result;
	}
	@Override
	protected void onPostExecute(ArrayList<Long> result) {
		if (cb != null)
			cb.updateData(result);
		super.onPostExecute(result);
	}
}
