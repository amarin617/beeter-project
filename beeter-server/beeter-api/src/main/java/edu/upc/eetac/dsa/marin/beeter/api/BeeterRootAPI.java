package edu.upc.eetac.dsa.marin.beeter.api;

import java.util.ArrayList;
import java.util.List;

import edu.upc.eetac.dsa.marin.beeter.api.links.Link;

public class BeeterRootAPI {
	
	private List<Link> links = new ArrayList<Link>();

	public void addLink(Link link) {
		links.add(link);
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
}
