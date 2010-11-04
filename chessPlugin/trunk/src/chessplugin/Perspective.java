package chessplugin;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import chessplugin.views.BlackView;
import chessplugin.views.ChessView;
import chessplugin.views.DiagnosticView;
import chessplugin.views.WhiteView;


public class Perspective implements IPerspectiveFactory {

	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		layout.setEditorAreaVisible(false);
		layout.setFixed(false);
				
		layout.addStandaloneView(ChessView.ID, true, IPageLayout.LEFT, 1.0f, editorArea);
		
		layout.addView(DiagnosticView.ID, IPageLayout.RIGHT, 0.9f, ChessView.ID);
		layout.addView(WhiteView.ID, IPageLayout.LEFT, 0.35f, ChessView.ID);
		layout.addView(BlackView.ID, IPageLayout.BOTTOM, 0.5f, WhiteView.ID);

		layout.getViewLayout(ChessView.ID).setCloseable(false);
		layout.getViewLayout(WhiteView.ID).setCloseable(false);
		layout.getViewLayout(BlackView.ID).setCloseable(false);
		
		
	}

}
