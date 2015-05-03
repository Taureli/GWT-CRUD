package com.jakub.projekt.client;

import java.util.List;

import com.jakub.projekt.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GWTcrud implements EntryPoint {
	/**
	 * The message displayed to the user when the server cannot be reached or
	 * returns an error.
	 */
	private static final String SERVER_ERROR = "An error occurred while "
			+ "attempting to contact the server. Please check your network "
			+ "connection and try again.";

	//remote service proxy
	private final CrudServiceAsync crudService = GWT.create(CrudService.class);
	
	//Little helpful variable for editing data
	int editID;

	//entry point method
	public void onModuleLoad() {
		final Button sendButton = new Button("Send");
		final TextBox nameField = new TextBox();
		nameField.setText("Sample data");
		final Label errorLabel = new Label();
		sendButton.addStyleName("sendButton");

		RootPanel.get("nameFieldContainer").add(nameField);
		RootPanel.get("sendButtonContainer").add(sendButton);
		RootPanel.get("errorLabelContainer").add(errorLabel);
		
		//My panel for displaying data
		//final FlowPanel dataList = new FlowPanel();
		//RootPanel.get().add(dataList);
		final RootPanel dataList = RootPanel.get("listField");
		
		// Focus the cursor on the name field when the app loads
		nameField.setFocus(true);
		nameField.selectAll();

		// Create the popup dialog box
		final DialogBox dialogBox = new DialogBox();
		dialogBox.setText("Remote Procedure Call");
		dialogBox.setAnimationEnabled(true);
		final Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		final Label textToServerLabel = new Label();
		final HTML serverResponseLabel = new HTML();
		VerticalPanel dialogVPanel = new VerticalPanel();
		dialogVPanel.addStyleName("dialogVPanel");
		dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
		dialogVPanel.add(serverResponseLabel);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		dialogVPanel.add(closeButton);
		dialogBox.setWidget(dialogVPanel);

		// Add a handler to close the DialogBox
		closeButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				dialogBox.hide();
				sendButton.setEnabled(true);
				sendButton.setFocus(true);
			}
		});
		
		//---------Edit data popup------------
		final DialogBox editor = new DialogBox();
		editor.setText("Edit data");
		editor.setAnimationEnabled(true);
		final Button closeEditButton = new Button("Close");
		VerticalPanel editPanel = new VerticalPanel();
		editPanel.addStyleName("dialogVPanel");
		
		final Button sendEditButton = new Button("Save");
		final TextBox nameEditField = new TextBox();
		editPanel.add(nameEditField);
		editPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
		editPanel.add(sendEditButton);
		editPanel.add(closeEditButton);
		editor.setWidget(editPanel);
		
		closeEditButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				editor.hide();
				sendButton.setEnabled(true);
				sendButton.setFocus(true);
			}
		});
		//--------------------------------

		// Create a handler for the sendButton and nameField
		class MyHandler implements ClickHandler {

			public void onClick(ClickEvent event) {
				sendDataToServer();
			}

			private void sendDataToServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = nameField.getText();
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter between 4 and 25 characters");
					return;
				}

				// Then, we send the input to the server.
				sendButton.setEnabled(false);
				textToServerLabel.setText(textToServer);
				serverResponseLabel.setText("");
				crudService.addData(textToServer,
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox.setText("Remote Procedure Call - Failure");
								serverResponseLabel.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(String result) {
								dialogBox.setText("Remote Procedure Call");
								serverResponseLabel.removeStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(result);
								dialogBox.center();
								closeButton.setFocus(true);
								getDataFromServer();
							}
						});
			}
			
			private void getDataFromServer() {
				crudService.getData(
						new AsyncCallback<List<String>>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox.setText("Remote Procedure Call - Failure");
								serverResponseLabel.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(final List<String> result) {
								dataList.clear();
								for(final String someText : result){
									
									HorizontalPanel panel = new HorizontalPanel();
									Button edit = new Button("E");
									Button remove = new Button("R");
									
									ClickHandler editHandler = new ClickHandler() {
										public void onClick(ClickEvent event) {
											editObj(result.indexOf(someText));
										}
									};
									ClickHandler removeHandler = new ClickHandler() {
										public void onClick(ClickEvent event) {
											removeObjFromServer(result.indexOf(someText));
										}
									};
									
									edit.addClickHandler(editHandler);
									remove.addClickHandler(removeHandler);
									
									panel.add(edit);
									panel.add(remove);
									panel.add(new HTML(someText));
									
									dataList.add(panel);
									
								}
							}
						});
			}
			
			private void editObj(int i){
				editID = i;
				editor.center();
				nameEditField.setFocus(true);
				sendButton.setEnabled(false);
			}
			
			private void removeObjFromServer(int i){
				crudService.removeData(i,
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox.setText("Remote Procedure Call - Failure");
								serverResponseLabel.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
							}

							public void onSuccess(String result) {
								dialogBox.setText("Remote Procedure Call");
								serverResponseLabel.removeStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(result);
								dialogBox.center();
								closeButton.setFocus(true);
								getDataFromServer();
							}
						});
			}
			
		}
		
		class EditHandler implements ClickHandler {

			@Override
			public void onClick(ClickEvent event) {
				updateDataOnServer();
			}
			
			private void updateDataOnServer() {
				// First, we validate the input.
				errorLabel.setText("");
				String textToServer = nameEditField.getText();
				if (!FieldVerifier.isValidName(textToServer)) {
					errorLabel.setText("Please enter between 4 and 25 characters");
					return;
				}

				// Then, we send the input to the server.
				sendEditButton.setEnabled(false);
				nameEditField.setText("");
				serverResponseLabel.setText("");
				crudService.editData(editID, textToServer,
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								// Show the RPC error message to the user
								dialogBox.setText("Remote Procedure Call - Failure");
								serverResponseLabel.addStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(SERVER_ERROR);
								dialogBox.center();
								closeButton.setFocus(true);
								sendEditButton.setEnabled(true);
							}

							public void onSuccess(String result) {
								dialogBox.setText("Remote Procedure Call");
								serverResponseLabel.removeStyleName("serverResponseLabelError");
								serverResponseLabel.setHTML(result);
								dialogBox.center();
								sendEditButton.setEnabled(true);
								editor.hide();
								closeButton.setFocus(true);
								MyHandler temp = new MyHandler();
								temp.getDataFromServer();
							}
						});
			}
		
		}

		// Add a handler to send the name to the server
		MyHandler handler = new MyHandler();
		sendButton.addClickHandler(handler);
		
		EditHandler editHandler = new EditHandler();
		sendEditButton.addClickHandler(editHandler);
		
		handler.getDataFromServer();
	}
}
