package tools.customforcedirected;

public class Edge extends Node{
	public Edge(Node from, String lbl, Node to) {
		super(lbl);
		this.from=from;
		this.to=to;
		this.charge = charge/10;
		this.mass = mass/10;
	}

	public Node from;
	public Node to;
	
	static double k = 40;
	static double len = 0.2;
	
}