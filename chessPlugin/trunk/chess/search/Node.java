package search;

import java.util.Arrays;
import java.util.List;

public class Node<T extends AdversarialState> {
	private final T state;
	private final Node<T> parent;
	private final int depth;
	private int cachedEvaluatedUtility;
	private int v;

	public Node(T state, Node<T> parent, int depth) {
		this.state = state;
		this.parent = parent;
		this.depth = depth;
		cachedEvaluatedUtility = state.utility();
	}

	public T getState() {
		return state;
	}

	Node<T> getParent() {
		return parent;
	}

	@SuppressWarnings("unchecked")
	public List<Node<T>> expand() {
		List<T> succesors = (List<T>) state.succesors();
		Node<T>[] ret = new Node[succesors.size()];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = new Node<T>(succesors.get(i), this, depth + 1);
		}
		return Arrays.asList(ret);
	}

	@SuppressWarnings("unchecked")
	public List<Node<T>> getPath() {
		Node<T> itr = this;
		Node<T>[] path = new Node[depth + 1];
		for (int d = depth; d >= 0; d--) {
			path[d] = itr;
			itr = itr.parent;
		}
		return Arrays.asList(path);
	}

	public int getDepth() {
		return depth;
	}

	@Override
	public String toString() {
		return state.toString();
	}

	public int getUtility() {
		return cachedEvaluatedUtility;
	}

	public void setV(int v) {
		this.v=v;
		
	}

	public int getV() {
		return v;
	}
}
