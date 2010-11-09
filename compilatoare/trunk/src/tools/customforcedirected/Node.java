package tools.customforcedirected;

public class Node{
	public Node(String label) {
		this.label = label;
	}
	public Node(Node node) {
		x=node.x;
		y=node.y;
		vx=node.vx;
		vy=node.vy;
		mass=node.mass;
		charge=node.charge;
	}
	public String label;

	double x=0,y=0;
	double vx=0,vy=0;
	double mass = 10;
	double charge=0.7;
}