package tools.customforcedirected;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

public class ForceDirectedFrame extends JFrame{
		
	private static final long serialVersionUID = 5402702283838316418L;
	public ForceDirectedPanel pnl;

	public ForceDirectedFrame(){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pnl = new ForceDirectedPanel();
		add(pnl);

		javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(pnl);
	        pnl.setLayout(jPanel1Layout);
	        jPanel1Layout.setHorizontalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGap(0, 752, Short.MAX_VALUE)
	        );
	        jPanel1Layout.setVerticalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGap(0, 552, Short.MAX_VALUE)
	        );

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(pnl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addContainerGap())
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(pnl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addContainerGap())
	        );
	        
	        pack();
	}
	
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ForceDirectedFrame frame = new ForceDirectedFrame();
                List<Edge> edges = new ArrayList<Edge>();
				List<Node> nodes =  new ArrayList<Node>();
				Node ana = new Node("ana");
				Node mere = new Node("mere");
				Node pere = new Node("pere");
				Edge are = new Edge(ana,"are",mere);				
				Edge nare = new Edge(ana,"nare",pere);
				Edge nus = new Edge(mere,"nus",pere);
				nodes.add(ana);
				nodes.add(mere);
				nodes.add(pere);
				edges.add(are);
				edges.add(nare);
				edges.add(nus);
				frame.pnl.setGraph(nodes, edges);
                frame.setVisible(true);
            }
        });
    }
}
