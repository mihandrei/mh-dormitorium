package chessplugin.widgets;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

public class Nebunesc extends Canvas {

	private Image image;

	Color white;

	public Nebunesc(Composite parent, int style) {
		super(parent, style);

		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				dispose();
			}

		});

		addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				Nebunesc.this.paintControl(e);
			}
		});

		addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Nebunesc.this.controlResized(e);
			}
		});
	}

	protected void controlResized(ControlEvent e) {
		redraw();
	}
	
	private double padding = 0.8;
	
	void paintControl(PaintEvent e) {
		GC gc = e.gc;
		if (image != null) {
			int destw =(int)( getBounds().width * padding);
			int desth =(int)( getBounds().height * padding);
			int destx =(int)( getBounds().width * (1- padding) / 2);
			int desty =(int)( getBounds().height * (1- padding) / 2);
			gc.drawImage(image, 0, 0, image.getBounds().width, image.getBounds().height,
					destx,desty,destw,desth);
		}
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
		redraw();
	}

	public void setPadding(double padding) {
		this.padding = padding;
	}

	public double getPadding() {
		return padding;
	}

}
