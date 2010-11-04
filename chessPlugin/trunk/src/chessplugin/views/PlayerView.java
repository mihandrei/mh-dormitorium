package chessplugin.views;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import chess.repres.Ox88.Piece;
import chessplugin.Activator;
import chessplugin.Model;
import chessplugin.widgets.Nebunesc;

public class PlayerView extends ViewPart {

	private Nebunesc[] stolenPieces = new Nebunesc[16];
	private int index=0;
	private Model model;
	protected boolean white;
	
	public PlayerView(){
		model = Activator.getDefault().getModel();
		model.addListener(new IPropertyChangeListener(){
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if(event.getProperty().equals("capture")){
					Piece p  = (Piece) event.getNewValue();
					if(p.iswhite == white)
						addStolenPiece(p);
				}
			}
		});		
	}
	
	@Override
	public void createPartControl(Composite parent) {
		GridLayout gl = new GridLayout(4, true);
		
		parent.setLayout(gl);
		
		GridData gridData;
		for (int i = 0; i < stolenPieces.length; i++) {
			stolenPieces[i] = new Nebunesc(parent, SWT.NONE);
			gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1,1);
			stolenPieces[i].setLayoutData(gridData);
		}
	}

	private void addStolenPiece(Piece p){
		Image image = Activator.getDefault().getImageRegistry().get(p.toString());
		stolenPieces[index].setImage(image);
		index++;
	}

	@Override
	public void setFocus() {

	}
}
