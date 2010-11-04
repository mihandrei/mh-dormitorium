package search;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * An alpha-beta searcher.
 * It shuffles the expansion of a node. 
 * @author miha
 *
 * @param <T> the type of the states in the search tree
 */
public class Adversarial<T extends AdversarialState> {
	private int cutoff = Integer.MAX_VALUE;
	private boolean utilityinv;
	int nodesexpanded, minpr, maxpr;

	/**
	 * contains the move succession that was decided the best. used for debugging/reporting purpose to justify the top
	 * level move decision.
	 */
	private List<Node<T>> plannedMoveSuccession;

	public Adversarial() {
	}

	public void setUtilityInv(boolean b) {
		utilityinv = b;
	}

	public Node<T> alphaBetaSearch(T state) {
		return alphaBetaSearch(state, Integer.MAX_VALUE);
	}

	public Node<T> alphaBetaSearch(T state, int cutoff) {
		this.cutoff = cutoff;
		nodesexpanded = 0;
		maxpr = minpr = 0;
		long t0 = System.nanoTime();
		Node<T> n = new Node<T>(state, null, 0);

		List<Node<T>> succ;
		if (!utilityinv)
			succ = max(n, Integer.MIN_VALUE, Integer.MAX_VALUE);
		else
			succ = min(n, Integer.MIN_VALUE, Integer.MAX_VALUE);

		plannedMoveSuccession = succ;
		t0 = System.nanoTime() - t0;
		System.out.println("nodes expanded: " + nodesexpanded);
		System.out.println("max/min prunned: " + maxpr + "/" + minpr);
		System.out.println("in " + (float) t0 / 1000000000 + " sec " + t0 / (nodesexpanded * 1000) + " microsec/node");
		return succ.get(succ.size() - 2);
	}

	private List<Node<T>> min(Node<T> n, int alpha, int beta) {
		List<Node<T>> ret = null;
		if (terminal_test(n)) {
			ret = new ArrayList<Node<T>>();
			n.setV(n.getUtility());
			ret.add(n);
			return ret;
		}

		int v = Integer.MAX_VALUE;

		List<Node<T>> expanded = n.expand();
		scramble(expanded);

		for (Node<T> node : expanded) {
			nodesexpanded++;
			List<Node<T>> maxSucc = max(node, alpha, beta);

			// the same as node but with the alpha beta node value set (v)
			Node<T> maxNode = maxSucc.get(maxSucc.size() - 1);

			if (maxNode.getV() < v) {
				v = maxNode.getV();
				ret = maxSucc;
			}

			if (v <= alpha) {
				minpr++;
				break;
			}
			beta = Math.min(beta, v);
		}
		assert ret != null;
		n.setV(v);
		ret.add(n);
		return ret;
	}

	Random r = new Random();

	private void scramble(List<Node<T>> expand) {
		int length = expand.size();
		for (int i = 0; i < length; i++) {
			int j = r.nextInt(length - i) + i;
			Node<T> tmp = expand.get(i);
			expand.set(i, expand.get(j));
			expand.set(j, tmp);
		}
	}

	/**
	 * keeps track of the best moves down the chain. the last entry in the returned list is the newest node (with lowest
	 * depth)
	 */
	private List<Node<T>> max(Node<T> n, int alpha, int beta) {
		List<Node<T>> ret = null;
		if (terminal_test(n)) {
			ret = new ArrayList<Node<T>>();
			n.setV(n.getUtility());
			ret.add(n);
			return ret;
		}

		int v = Integer.MIN_VALUE;

		List<Node<T>> expanded = n.expand();
		scramble(expanded);

		for (Node<T> node : expanded ) {
			nodesexpanded++;
			List<Node<T>> minSucc = min(node, alpha, beta);
			Node<T> minNode = minSucc.get(minSucc.size() - 1);

			if (minNode.getV() > v) {
				v = minNode.getV();
				ret = minSucc;
			}

			if (v >= beta) {
				maxpr++;
				break;
			}
			alpha = Math.max(alpha, v);
		}
		assert ret != null;
		n.setV(v);
		ret.add(n);
		return ret;
	}

	private boolean terminal_test(Node<T> n) {
		return (n.getDepth() >= cutoff) || (n.getState().issolution());
	}

	public List<Node<T>> getPlannedSuccession() {
		return plannedMoveSuccession;
	}
}
