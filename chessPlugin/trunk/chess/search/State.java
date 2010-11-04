package search;

import java.util.List;

public interface State{

	public abstract List<? extends State> succesors();
	public boolean issolution();
}
