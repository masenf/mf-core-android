package com.masenf.core.async;

import java.util.ArrayList;

import android.database.sqlite.SQLiteException;

import com.masenf.core.async.callbacks.DataReadCallback;
import com.masenf.core.data.BaseEntry;
import com.masenf.core.data.DataQuery;
import com.masenf.core.data.EntryList;

public class DataReadTask extends ProgressReportingTask<DataQuery, EntryList> {

	private DataReadCallback cb;
	public DataReadTask(DataReadCallback cb) {
		this.cb = cb;
	}
	@Override
	protected EntryList doInBackground(DataQuery... params) {
		EntryList result = new EntryList();

		postProgressMax(params.length);
		for (int i=0;i<params.length;i++)
		{
			DataQuery q = params[i];
			ArrayList<BaseEntry> intermediate = null;
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
		postStatus("Fetched " + result.size() + " records");
		return result;
	}
	@Override
	protected void onPostExecute(EntryList result) {
		cb.updateData(result);
		super.onPostExecute(result);
	}
}
