package com.masenf.core;

import android.view.View;

/** This is the interface for a self-drawing item. Implement getViewLayout to return
 * the layout Id for this object's view. 
 * Implement updateView to manipulate the view before it is returned to the ListView.
 * convertView will always be a non-null, inflated instance of the Id returned.
 */
public interface DrawingItem {

	
	/** 
	 * the adapter will inflate whichever layout resource this function returns
	 * @return the resource id of the layout for this item
	 */
	public int getViewLayout();
	
	/**
	 * update the view for this entry<br><br>
	 * note: the view's tag MUST be set to the Item's class for recycling purposes
	 * @param convertView the possibly recycled view
	 * @return the populated view for the item
	 */
	public View updateView(View convertView);
}
