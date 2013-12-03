package edu.upc.eetac.dsa.marin.beeter.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import edu.upc.eetac.dsa.marin.beeter.api.links.BeeterAPILinkBuilder;
import edu.upc.eetac.dsa.marin.beeter.api.links.Link;

@Path("/")
public class BeeterRootAPIResource {
	@Context
	private UriInfo uriInfo;

	// TODO: Return links
	@GET
	@Produces(MediaType.BEETER_API_LINK_COLLECTION)
	public BeeterRootAPI getLinks(){
		BeeterRootAPI root = new BeeterRootAPI();
		root.addLink(BeeterAPILinkBuilder.buildURIRootAPI(uriInfo));
		root.addLink(BeeterAPILinkBuilder.buildTemplatedURIStings(uriInfo, "stings"));
		root.addLink(BeeterAPILinkBuilder.buildTemplatedURIStings(uriInfo, "stings", true));
		root.addLink(BeeterAPILinkBuilder.buildURIStings(uriInfo, "create"));
		return root;
	}
}