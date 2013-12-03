package edu.upc.eetac.dsa.marin.beeter.api.links;

import java.net.URI;

import javax.ws.rs.core.UriInfo;

import edu.upc.eetac.dsa.marin.beeter.api.BeeterRootAPIResource;
import edu.upc.eetac.dsa.marin.beeter.api.MediaType;
import edu.upc.eetac.dsa.marin.beeter.api.StingResource;
import edu.upc.eetac.dsa.marin.beeter.api.model.Sting;

public class BeeterAPILinkBuilder {

	//La uriinfo es que cada recurso tiene la uri para acceder
	//aqui quiero construir el enlace a urirootapi
	
	public final static Link buildURIRootAPI(UriInfo uriInfo) {
		URI uriRoot = uriInfo.getBaseUriBuilder()
				.path(BeeterRootAPIResource.class).build();
		Link link = new Link();
		link.setUri(uriRoot.toString());
		link.setRel("self bookmark");//esta sera la pagina del cliente incial
		link.setTitle("Beeter API");
		link.setType(MediaType.BEETER_API_LINK_COLLECTION);

		return link;
	}

	public static final Link buildURIStings(UriInfo uriInfo, String rel) {
		return buildURIStings(uriInfo, null, null, null, rel);
	}

	public static final Link buildURIStings(UriInfo uriInfo, String offset,
			String length, String username, String rel) {
		URI uriStings;
		if (offset == null && length == null)
			uriStings = uriInfo.getBaseUriBuilder().path(StingResource.class)
					.build();
		else {
			if (username == null)
				uriStings = uriInfo.getBaseUriBuilder()
						.path(StingResource.class).queryParam("offset", offset)
						.queryParam("length", length).build();
			else
				uriStings = uriInfo.getBaseUriBuilder()
						.path(StingResource.class).queryParam("offset", offset)
						.queryParam("length", length)
						.queryParam("username", username).build();
		}

		Link self = new Link();
		self.setUri(uriStings.toString());
		self.setRel(rel);
		self.setTitle("Stings collection");
		self.setType(MediaType.BEETER_API_STING_COLLECTION);

		return self;
	}

	public static final Link buildTemplatedURIStings(UriInfo uriInfo, String rel) {

		return buildTemplatedURIStings(uriInfo, rel, false);
	}

	public static final Link buildTemplatedURIStings(UriInfo uriInfo,
			String rel, boolean username) {
		URI uriStings;
		if (username)
			uriStings = uriInfo.getBaseUriBuilder().path(StingResource.class)
					.queryParam("offset", "{offset}")
					.queryParam("length", "{length}")
					.queryParam("username", "{username}").build();
		else
			uriStings = uriInfo.getBaseUriBuilder().path(StingResource.class)
					.queryParam("offset", "{offset}")
					.queryParam("length", "{length}").build();

		Link link = new Link();
		link.setUri(URITemplateBuilder.buildTemplatedURI(uriStings));
		link.setRel(rel);
		if (username)
			link.setTitle("Stings collection resource filtered by {username}");
		else
			link.setTitle("Stings collection resource");
		link.setType(MediaType.BEETER_API_STING_COLLECTION);

		return link;
	}

	public final static Link buildURISting(UriInfo uriInfo, Sting sting) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(StingResource.class).build();
		Link link = new Link();
		link.setUri(stingURI.toString());
		link.setRel("self");
		link.setTitle("Sting " + sting.getStingid());
		link.setType(MediaType.BEETER_API_STING);

		return link;
	}

	public final static Link buildURIStingId(UriInfo uriInfo, String stingid,
			String rel) {
		URI stingURI = uriInfo.getBaseUriBuilder().path(StingResource.class)
				.path(StingResource.class, "getSting").build(stingid);
		Link link = new Link();//el getsting, podria ser el del update y el del delete
		link.setUri(stingURI.toString());
		link.setRel("self");
		link.setTitle("Sting " + stingid);
		link.setType(MediaType.BEETER_API_STING);

		return link;
	}

}
