package chessplugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import chess.ComputerPlayer;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	ComputerPlayer comppl;
	// The plug-in ID
	public static final String PLUGIN_ID = "chessPlugin";

	// The shared instance
	private static Activator plugin;
	
	private Model model;

	public ComputerPlayer computer;
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		model = new Model();
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		getImageRegistry().dispose();
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public Model getModel() {
		return model;
	}
	@Override
	protected ImageRegistry createImageRegistry() {
		ImageRegistry registry = super.createImageRegistry();
		registry.put("R", ImageDescriptor.createFromFile(getClass(), "/icons/chess/Yellow R.gif"));	
		registry.put("N", ImageDescriptor.createFromFile(getClass(),"/icons/chess/Yellow N.gif"));
		registry.put("B", ImageDescriptor.createFromFile(getClass(),"/icons/chess/Yellow B.gif"));
		registry.put("K", ImageDescriptor.createFromFile(getClass(),"/icons/chess/Yellow K.gif"));
		registry.put("P", ImageDescriptor.createFromFile(getClass(),"/icons/chess/Yellow P.gif"));
		registry.put("Q", ImageDescriptor.createFromFile(getClass(),"/icons/chess/Yellow Q.gif"));
		registry.put("r", ImageDescriptor.createFromFile(getClass(),"/icons/chess/Black R.gif"));
		registry.put("n", ImageDescriptor.createFromFile(getClass(),"/icons/chess/Black N.gif"));
		registry.put("b", ImageDescriptor.createFromFile(getClass(),"/icons/chess/Black B.gif"));
		registry.put("k", ImageDescriptor.createFromFile(getClass(),"/icons/chess/Black K.gif"));
		registry.put("p", ImageDescriptor.createFromFile(getClass(),"/icons/chess/Black P.gif"));
		registry.put("q", ImageDescriptor.createFromFile(getClass(),"/icons/chess/Black Q.gif"));
		return registry;
	}
	
}
