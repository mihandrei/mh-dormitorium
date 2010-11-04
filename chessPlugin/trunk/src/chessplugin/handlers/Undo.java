package chessplugin.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import chessplugin.Activator;

public class Undo extends AbstractHandler {

	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Activator plugin = Activator.getDefault();
		plugin.getModel().undo();
		return null;
	}

}
