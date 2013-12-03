package edu.upc.eetac.dsa.marin.beeter.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import edu.upc.eetac.dsa.marin.beeter.api.model.BeeterError;

public class UserNotFoundException extends WebApplicationException {
	private final static String MESSAGE = "User not found";

	public UserNotFoundException() {
		super(Response
				.status(Response.Status.NOT_FOUND)
				.entity(new BeeterError(Response.Status.NOT_FOUND
						.getStatusCode(), MESSAGE))
				.type(MediaType.BEETER_API_ERROR).build());
	}

}