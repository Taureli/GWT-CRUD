package com.jakub.projekt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface CrudServiceAsync {
	
	void addData(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;
}
