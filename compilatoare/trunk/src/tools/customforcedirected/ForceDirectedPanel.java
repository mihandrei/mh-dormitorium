package tools.customforcedirected;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

public class ForceDirectedPanel extends JPanel implements ActionListener{
    private static final long serialVersionUID = -2257190772250283891L;
    private ForceLayout force = new ForceLayout();
    private Timer timer = new Timer(100, this);
    
    @Override
    public void actionPerformed(ActionEvent arg0) {			
    	double energy = force.computeLayout();
    	if(energy<0.1){
    		timer.stop();
    	}
    	repaint();
    }
    
    void onGraphChanged(){
    	timer.start();   
    }
    
    public void setGraph(List<Node> nodes, List<Edge> edges){
    	force.setGraph(nodes, edges);
    	force.randomize();
    	onGraphChanged();
    }
    
	@Override
	public void paint(Graphics g){
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        int w = getWidth();
        int h = getHeight();
        g2d.clearRect(0, 0,w,h);
                
        for(Node n:force.nodes){
        	if(n instanceof Edge){
        		Edge ne = (Edge) n;
        		g2d.setColor(Color.GRAY);
        		//QuadCurve2D curve = new QuadCurve2D.Double();        		
                //curve.setCurve(w*ne.from.x, h*ne.from.y, w*n.x, h*n.y, w*ne.to.x, h*ne.to.y);
        		Line2D curve = new Line2D.Double();
        		curve.setLine(w*ne.from.x, h*ne.from.y, w*n.x, h*n.y);
        		g2d.draw(curve);
        		curve.setLine(w*n.x, h*n.y,w*ne.to.x, h*ne.to.y);
        		g2d.draw(curve);
        		
                g2d.drawString(n.label, (int)(w*n.x), (int)(h*n.y));
                g2d.draw(new Ellipse2D.Double(w*n.x, h*n.y,1,1));
        	}else{
        		g2d.setColor(Color.BLACK);
        		g2d.drawString(n.label, (int)(n.x*w), (int)(n.y*h));
        		g2d.draw(new Ellipse2D.Double(n.x*w-20,n.y*h-20, 40, 40));
        		//g2d.drawOval(n.x+2, n.y+2, 84, 84);
        	}
        }
	}
}