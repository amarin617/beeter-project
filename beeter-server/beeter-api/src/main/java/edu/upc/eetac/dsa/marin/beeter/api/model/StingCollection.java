package edu.upc.eetac.dsa.marin.beeter.api.model;

import java.util.ArrayList;
import java.util.List;

import edu.upc.eetac.dsa.marin.beeter.api.links.Link;

public class StingCollection {
	private List<Sting> stings;
	private List<Link> links = new ArrayList<Link>();

	public StingCollection() {
		super();
		stings = new ArrayList<Sting>();
	}

	public List<Sting> getStings() {
		return stings;
	}

	public void setStings(List<Sting> stings) {
		this.stings = stings;
	}

	public void addSting(Sting sting) {
		stings.add(sting);
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	public void addLink(Link link){
		links.add(link);
	}
}
