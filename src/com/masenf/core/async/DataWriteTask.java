package com.masenf.core.async;

import java.util.ArrayList;
import java.util.UUID;

import com.masenf.core.async.callbacks.DataReadCallback;
import com.masenf.core.async.callbacks.DataWriteCallback;
import com.masenf.core.data.BaseEntry;
import com.masenf.core.data.DataQuery;
import com.masenf.core.data.DataUpdateQuery;
import com.masenf.core.data.EntryList;

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
			ArrayList<Long> intermediate = q.execute();
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
