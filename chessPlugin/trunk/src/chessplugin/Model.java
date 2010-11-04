package chessplugin;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.PlatformUI;

import search.Node;
import chess.ComputerPlayer;
import chess.repres.Ox88.Board;
import chess.repres.Ox88.Coordinate;
import chess.repres.Ox88.Move;
import chess.repres.Ox88.Piece;
import chess.search.ChessState;

public class Model {
	private Board board;
	private Deque<Board> history;
	private ComputerPlayer computer;

	public Model() {
		init();
	}

	private void init() {
		board = Board.createDefaultBoard();
		board.setIsWhiteToMove(false);
		computer = new ComputerPlayer(false);
		computer.setDepth(5);
		history = new ArrayDeque<Board>();
		startPlay();
	}

	public void newGame() {
		init();
		dispatchChangeEvent("board", null, board);
	}

	public Board getBoard() {
		return board;
	}

	private List<IPropertyChangeListener> listeners = new ArrayList<IPropertyChangeListener>();

	public void addListener(IPropertyChangeListener listener) {
		listeners.add(listener);
	}

	public void removeListener(IPropertyChangeListener listener) {
		listeners.remove(listener);
	}

	private void dispatchChangeEvent(String changedProperty, Object oldV,
			Object newV) {
		for (IPropertyChangeListener listener : listeners) {
			PropertyChangeEvent event = new PropertyChangeEvent(this,
					changedProperty, oldV, newV);
			listener.propertyChange(event);
		}
	}

	public void undo() {
		if (history.size() != 0) {
			board = history.pop();
		}
		dispatchChangeEvent("board", null, board);
	}

	public List<Move> getAllMoves(Piece p) {
		return board.genMoves(p);
	}

	public Piece getPiece(Coordinate clickCoord) {
		return board.getPiece(clickCoord);
	}

	public boolean trymove(Piece selectedpiece, Coordinate clickCoord) {
		Move move = board.move(selectedpiece, clickCoord);
		if (move != null) {
			history.push(board);
			board = move.newstate;
			dispatchChangeEvent("board", null, board);
			Piece capture = move.capturedPiece;
			if (capture != null)
				dispatchChangeEvent("capture", null, capture);
			return true;
		}
		return false;
	}

	public void startPlay() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				Move move = computer.play(board);
				reportResultToUI(move);
			}
		};
		new Thread(runnable).start();
	}

	protected void reportResultToUI(final Move move) {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (move != null) {
					history.push(board);
					board = move.newstate;
					dispatchChangeEvent("board", null, board);

					Piece capture = move.capturedPiece;
					if (capture != null)
						dispatchChangeEvent("capture", null, capture);

					String logReason = "";

					for (Node<ChessState> n : computer.getEngine()
							.getPlannedSuccession()) {
						logReason += n.toString() + "\n ut:" + n.getUtility()
								+ "\n";
					}
					msglog = logReason;

					dispatchChangeEvent("log", "", msglog);
				}
			}
		});

	}

	private String msglog = "";

	public String getLog() {
		return msglog;
	}
}
