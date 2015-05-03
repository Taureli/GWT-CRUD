package com.jakub.projekt.server;

import java.util.ArrayList;
import java.util.List;

import com.jakub.projekt.client.CrudService;
import com.jakub.projekt.shared.FieldVerifier;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class CrudServiceImpl extends RemoteServiceServlet implements
		CrudService {
	
	public List<String> database = new ArrayList<String>();

	public String addData(String input) throws IllegalArgumentException {
		// Verify that the input is valid. 
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException(
					"Has to be 4-25 characters long");
		}

		// Escape data from the client to avoid cross-site script vulnerabilities.
		input = escapeHtml(input);
		
		database.add(input);

		return "New data added succesfully!";
	}

	@Override
	public List<String> getData() {
		return database;
	}

	@Override
	public String removeData(int i) {
		database.remove(i);
		return "Data was removed succesfully!";
	}
	
	@Override
	public String editData(int id, String newName) {
		database.set(id, newName);
		return "Data was updated succesfully!";
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
}
