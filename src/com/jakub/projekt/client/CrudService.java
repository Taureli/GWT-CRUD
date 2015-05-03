package com.jakub.projekt.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * The client side stub for the RPC service.
 */
public interface CrudService extends RemoteService {
	String addData(String name) throws IllegalArgumentException;
	List<String> getData();
	String removeData(int i);
}
