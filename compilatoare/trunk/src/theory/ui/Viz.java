package theory.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import theory.NDFA;
import tools.GraphViz;
import tools.customforcedirected.Edge;
import tools.customforcedirected.ForceDirectedFrame;
import tools.customforcedirected.Node;

public class Viz {
	public void el_show(final NDFA ndfa){
	    java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ForceDirectedFrame frame = new ForceDirectedFrame();
                List<Edge> edges = new ArrayList<Edge>();
				List<Node> nodes =  new ArrayList<Node>();
		
				Map<String,Node> sn = new HashMap<String, Node>();
				for (String state : ndfa.Q) {					
					sn.put(state,new Node(state));
				}
				nodes.addAll(sn.values());
				
				for (String state : ndfa.Q) {									
					Map<String, Set<String>> symtran = ndfa.transitions.get(state);
					for (String sym : symtran.keySet()) {
						for (String dest : symtran.get(sym)) {
							edges.add(new Edge(sn.get(state),sym,sn.get(dest)));
						}
					}
				}  
				
				frame.pnl.setGraph(nodes, edges);
                frame.setVisible(true);
            }
        });
	}
	public static void show(final NDFA ndfa){
	java.awt.EventQueue.invokeLater(new Runnable() {
		public void run() {
			ImageIcon ico = render_dot( ndfa);
			JLabel lbl = new JLabel(ico);
			JFrame frm = new JFrame();
			frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frm.add(lbl);
			frm.pack();
			frm.setVisible(true);
		}
	});
	}
	public static ImageIcon render_dot(NDFA ndfa) {
		StringBuilder dot = new StringBuilder();
		dot.append( "digraph dfa {\n" + "rankdir=LR;\n" + "size=\"12\";\n"
				+ "ranksep = 1.5\n;" + "nodesep = .25;\n" + "\"\" [shape=none]");

		for (String state : ndfa.Q) {
			String shape = ndfa.F.contains(state) ? "doublecircle" : "circle";
			dot.append( String.format("\"%s\" [shape=%s];\n", state, shape));
		}

		dot.append( String.format("\"\" -> \"%s\"\n", ndfa.q0));

		for (String state : ndfa.Q) {
			Map<String, Set<String>> symtran = ndfa.transitions.get(state);
			for (String sym : symtran.keySet()) {
				for (String dest : symtran.get(sym)) {
					dot.append( String.format("\"%s\" -> \"%s\" [label=\"%s\"];\n ",
							state, dest, sym));
				}
			}
		}
		dot.append("}");

		final byte[] imgdata = new GraphViz().getGraph(dot.toString());
        return  new ImageIcon(imgdata);
	}
}
