package chessplugin.views;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import chessplugin.Activator;
import chessplugin.Model;

public class DiagnosticView extends ViewPart {
	public static final String ID = "chessPlugin.DiagnosticView";
	private Text console;
	private Model model;
	
	public DiagnosticView() {
		model = Activator.getDefault().getModel();
		model.addListener(new IPropertyChangeListener(){
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if(event.getProperty().equals("log")){
					console.setText(event.getNewValue().toString()+"\n");
				}
			}
		});		
	}
	@Override
	public void createPartControl(Composite parent) {
		console = new Text(parent, SWT.READ_ONLY | SWT.MULTI |SWT.V_SCROLL);		
		Font font = new Font(parent.getDisplay(),"Monospace",9 ,SWT.NORMAL);
		console.setFont(font);
		font.dispose();
	}

	@Override
	public void setFocus() {

	}

}
