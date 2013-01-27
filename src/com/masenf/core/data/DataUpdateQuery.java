package com.masenf.core.data;

import java.util.ArrayList;

public interface DataUpdateQuery {
	// execute returns a list of rows modified
	public ArrayList<Long> execute();
}
