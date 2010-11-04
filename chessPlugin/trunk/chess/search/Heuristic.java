package search;
/**
 * A Heuristic gives a approximate value of the current state compared to it's siblings.
 * This heuristic is meant to be used for node reordering so that an alpha beta cutoff will occur sooner.
 * Being called for each node expansion it must do verry inexpensive computation. 
 * This in contrast with Adversarialstate utility() which is called only for the terminal nodes.
 * @author mihand
 *
 * @param <T>
 */
public interface Heuristic<T extends AdversarialState> {

	int getH(Node<T> o2);

}
