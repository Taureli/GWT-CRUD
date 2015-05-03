package com.jakub.projekt.client;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart
 */
public interface CrudServiceAsync {
	
	void addData(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void getData(AsyncCallback<List<String>> asyncCallback);

	void removeData(int i, AsyncCallback<String> asyncCallback);

	void editData(int editID, String textToServer,
			AsyncCallback<String> asyncCallback);
}
