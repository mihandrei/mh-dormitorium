package tools.customforcedirected;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ForceLayout {
	List<Node> nodes =  new ArrayList<Node>();    
	Map<Node,Set<Node>> adjacency =  new HashMap<Node, Set<Node>>();
    
    double dt = 0.1;    
    double damping = 0.8;    
    double g = 0.5;
    
    public void randomize(){
    	Random random = new Random();
    	for(Node n:nodes){
    		n.x = random.nextDouble();
    		n.y = random.nextDouble();
    		n.vx = 0;
    		n.vy = 0;
    	}
    }
   
    public void setGraph(List<Node> nodes, List<Edge> edges){    	
        this.nodes = nodes;
        this.nodes.addAll(edges);
        for(Node n:nodes){
        	adjacency.put(n, new HashSet<Node>());
        }
        //fiecare legatura ii inlocuita de un nod de legatura si 2 arce
        //de fapt transformam multigrafu in-trun graf
        for(Edge edge:edges){     
        	adjacency.get(edge.from).add(edge);
        	adjacency.get(edge).add(edge.to);
        }
    }
    
    private boolean connected(Node n1,Node n2){
    	return adjacency.get(n1).contains(n2); 
    }

    public double computeLayout(){
		double kineticenergy = 0;
		List<Node> sourcegrid = new ArrayList<Node>();
		for(Node node:nodes){
			Node newnode = new Node(node);
			sourcegrid.add(newnode);
		}
		
		for(int i =0;i<sourcegrid.size();i++){
		    Node n1 = sourcegrid.get(i);
			double fx = 0,fy = 0; //force vector
			for(int j =0;j<sourcegrid.size();j++){
				if(i!=j){
					Node n2 = sourcegrid.get(j);
				//repulsion
					double distance2 =  Math.pow(n2.x - n1.x,2)+Math.pow(n2.y - n1.y,2);
					double repulsion = - n1.charge*n2.charge/distance2;
					//springs
					double spring =0;
					if (connected(nodes.get(i),nodes.get(j))){
						spring = Edge.k*(Math.sqrt(distance2) - Edge.len);
					}			
					fx += (repulsion+spring)*(n2.x-n1.x);
					fy += (repulsion+spring)*(n2.y-n1.y);
				}
			}	
			
			//global gravity, constanta dar centrala
			fx+= - g*Math.signum(n1.x - 0.5);
			fy+= - g*Math.signum(n1.y -0.5);
			
			//recalculeaza viteza, aceleratie constanta => dv = a*dt
			Node n = nodes.get(i);
			n.vx += fx/n.mass*dt;
			n.vy += fy/n.mass*dt;
			n.vx *=damping;
			n.vy *=damping;
			n.x += n.vx * dt;
			n.y += n.vy * dt;
			System.out.println(String.format("%f %f %f %f",n.x,n.y,n.vx,n.vy));
			kineticenergy += n.mass* (n.vx*n.vx+n.vy*n.vy)/2;
		}
		return kineticenergy;
	}
}
