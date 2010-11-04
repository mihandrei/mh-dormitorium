package chessplugin.views;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import chess.repres.Ox88.Coordinate;
import chess.repres.Ox88.Move;
import chess.repres.Ox88.Piece;
import chessplugin.Activator;
import chessplugin.Model;
import chessplugin.widgets.Nebunesc;

/**
 * 
 * a prototypical view of the board. TODO: refactor model out of the view TODO:
 * cleanup coordinate models TODO: move computation in a worker thread.
 * 
 * @author miha
 * 
 */
public class ChessView extends ViewPart {

	public static final String ID = "chessplugin.ChessView";

	private Nebunesc[] buttons = new Nebunesc[64];

	Composite parent;

	Model model;

	public ChessView() {
		model = Activator.getDefault().getModel();
		model.addListener(new IPropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals("board")) {
					frozen = false;
					System.out.println("unfrozen");
					drawView();
				}
			}
		});
	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;

		parent.setBackground(new Color(parent.getDisplay(), 219, 220, 253));
		GridLayout g1 = new GridLayout(8, true);
		g1.horizontalSpacing = 0;
		g1.verticalSpacing = 0;
		g1.marginHeight = 0;
		parent.setLayout(g1);

		GridData gridData;

		for (int i = 0; i < buttons.length; i++) {
			buttons[i] = new Nebunesc(parent, SWT.NONE);
			buttons[i].addMouseListener(new OurMouseListener(buttons[i]));
			buttons[i].setData(i);
			gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
			buttons[i].setLayoutData(gridData);
		}

		drawView();
	}

	int[] highlighted = new int[64];

	int[] attacked = new int[64];

	int selected;

	Piece selectedpiece;

	public boolean frozen = false;

	private void drawView() {
		float alfa = 0.05f;
		float alfa2 = 0.5f;
		for (int i = 0; i < buttons.length; i++) {

			RGB c1 = new RGB(255, 255, 255);
			RGB c2 = new RGB(0, 0, 0);

			RGB highlight;

			if (highlighted[i] != 0) {
				highlight = highlighted[i] == 2 ? new RGB(254, 130, 130)
						: new RGB(130, 130, 254);

				c1 = alphablend(new RGB(192, 192, 192), highlight, alfa);
				c2 = alphablend(new RGB(64, 64, 64), highlight, alfa);
			}

			if (i == selected && selectedpiece != null) {
				highlight = selectedpiece.iswhite ? new RGB(0, 0, 254)
						: new RGB(254, 0, 0);

				c1 = alphablend(new RGB(192, 192, 192), highlight, alfa2);
				c2 = alphablend(new RGB(64, 64, 64), highlight, alfa2);
			}

			if (attacked[i] != 0) {
				highlight = !selectedpiece.iswhite ? new RGB(0, 0, 254)
						: new RGB(254, 0, 0);
				c1 = alphablend(new RGB(192, 192, 192), highlight, alfa2);
				c2 = alphablend(new RGB(64, 64, 64), highlight, alfa2);
			}

			buttons[i].setImage(null);
			RGB back = (((i & 0x8) >> 3 ^ i & 0x1) == 0) ? c1 : c2;
			Color backColor = new Color(parent.getDisplay(), back);
			buttons[i].setBackground(backColor);
			backColor.dispose();

			Piece p = model.getPiece(new Coordinate(1 + 7 - i / 8, 1 + i % 8));
			if (p != null) {
				Image image = Activator.getDefault().getImageRegistry().get(
						p.toString());
				buttons[i].setImage(image);
			}

		}
	}

	RGB alphablend(RGB back, RGB fore, float alpha) {
		int redchannel = (int) (back.red * (1 - alpha) + fore.red * alpha);
		int greenchannel = (int) (back.green * (1 - alpha) + fore.green * alpha);
		int bluechannel = (int) (back.blue * (1 - alpha) + fore.blue * alpha);
		return new RGB(redchannel, greenchannel, bluechannel);
	}

	@Override
	public void setFocus() {

	}

	class OurMouseListener extends MouseAdapter {

		boolean ros = false;

		public OurMouseListener(Nebunesc nebunesc) {
		}

		private void handleSelect(Piece p) {
			selectedpiece = p;
			int loc = p.getLocation();
			selected = loc % 16 + (7 - loc / 16) * 8;

			List<Move> moves = model.getAllMoves(p);
			for (Move m : moves) {
				int l = m.newpiece.getLocation();
				int ox88dest = l % 16 + (7 - l / 16) * 8;
				highlighted[ox88dest] = p.iswhite ? 1 : 2;
				if (m.capturedPiece != null)
					attacked[ox88dest] = p.iswhite ? 1 : 2;
			}
		}

		@Override
		public void mouseDown(MouseEvent e) {
			// [0..63]
			Arrays.fill(highlighted, 0);
			Arrays.fill(attacked, 0);

			try {
				int indexClick = (Integer) ((Nebunesc) e.getSource()).getData();
				Coordinate clickCoord = new Coordinate(1 + 7 - indexClick / 8,
						1 + indexClick % 8);
				Piece p = model.getPiece(clickCoord);

				boolean move_is_selection = p != null
						&& (selectedpiece == null || selectedpiece.iswhite == p.iswhite);

				if (move_is_selection) {
					handleSelect(p);
					drawView(); // model has not changed but rendering data has,
								// the model doeas not keep yet highlight info;
								// this is in a way specific to the view
				} else if (selectedpiece != null) {// move
					// TODO: move these actions that WRITE to the model into a
					// controller!
					// raise event: move
					if (!frozen) {
						if (model.trymove(selectedpiece, clickCoord)) {
							frozen = true;
							System.out.println("frozen");
							model.startPlay();
						}
					}
					selectedpiece = null;
				}
			} catch (Exception erer) {
				erer.printStackTrace();
			}
		}

	}

}