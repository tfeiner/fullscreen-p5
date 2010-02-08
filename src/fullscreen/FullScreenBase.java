package fullscreen;

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PConstants;


/**
 * The base class {@link FullScreen} and {@link SoftFullScreen} inherit from. <br /> 
 * 
 * It defines some common methods that you might wanna dig through (like {@link FullScreenBase#setShortcutsEnabled(boolean)})
 * @author hansi
 */
public abstract class FullScreenBase {

	// Our daddie
	private final PApplet dad;
	
	// Cmd (apple) or ctrl (windows/linux) 
	final static int fsControlKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	
	// Enable key events?
	private boolean enableKeyEvents = true;
	
	// The previous key event
	static KeyEvent lastEvent = null;
	
	// Is opengl being used in this sketch? 
	public final boolean isGL; 
	
	
	// Key Listener
	KeyListener keyListener = new KeyAdapter(){
		public void keyPressed( KeyEvent e ){ keyEvent( e ); }
	}; 
	
	// Window listener
	WindowListener windowListener = new WindowAdapter(){
		public void windowClosing( WindowEvent e ){
			dad.exit();
		}
	};
	
	
	/**
	 * Create a fullscreen thingie
	 */
	public FullScreenBase( PApplet dad ){
		this.dad = dad;
		
		// Listen to processings key events
		dad.registerKeyEvent( this );

		// See if the graphics object somehow inherits from PGraphicsOpenGL
		isGL = FullScreenTools.isGL( dad ); 
		if( isGL ){
			// Make ppl aware that gl doesn't always work!
			System.err.println( "FullScreen API: Warning, OPENGL Support is experimental! " ); 
			System.err.println( "Keep checking http://www.superduper.org/processing/fullscreen_api/ for updates!" );
		}
	}
	
	
	/** 
	 * Enters/Leaves fullscreen mode
	 */
	public abstract void setFullScreen( boolean state );
	
	
	/**
	 * Are we currently in fullscreen mode? 
	 */
	public abstract boolean isFullScreen(); 
	
	
	/**
	 * Set resolution
	 * 
	 * @param xRes x resolution
	 * @param yRes y resolution
	 */
	public abstract void setResolution( int xRes, int yRes );  


	/**
	 * Enters fullscreen mode
	 */
	public void enter(){
		setFullScreen( true ); 
	}
	
	
	/**
	 * Leaves fullscreen mode
	 */
	public void leave(){
		setFullScreen( false ); 
	}
		
	/**
	 * Allow shortcuts?
	 * 
	 * @param state yes if true, no if false. 
	 */
	public void setShortcutsEnabled( boolean state ){
		enableKeyEvents = state; 
	}
	
	
	/**
	 * Whatever frame this specific implementation uses, it has to be registered here so that key events can be caught
	 */
	protected void registerFrame( Frame f ){
		f.addKeyListener( keyListener );
		f.addWindowListener( windowListener );  
	}
	
	protected void unregisterFrame( Frame f ){
		f.removeKeyListener( keyListener );
		f.removeWindowListener( windowListener );
	}



	/**
	 * Implement keyEvent to listen to processing's key events
	 * @param e
	 */
	public void keyEvent( KeyEvent e ){
		if( e.equals( lastEvent ) || !enableKeyEvents ){
			return; 
		}
		
		lastEvent = e; 
		
		// Catch the ESC key if in fullscreen mode
		if( e.getKeyCode() == KeyEvent.VK_ESCAPE ){
			if( isFullScreen() ){
				if( e.getID() == KeyEvent.KEY_RELEASED ){
					setFullScreen( false ); 
				}
			}
		}
		
		// catch the CMD+F combination (ALT+ENTER or CTRL+F for windows)
		else if( e.getID() == KeyEvent.KEY_PRESSED ){
			if( ( e.getKeyCode() == KeyEvent.VK_F && e.getModifiers() == fsControlKey ) ||
					( PApplet.platform == PConstants.WINDOWS && e.getKeyCode() == KeyEvent.VK_ENTER && e.getModifiers() == KeyEvent.VK_ALT ) ){
				// toggle fullscreen! 
				setFullScreen( !isFullScreen() ); 
			}
		}
	}
	
	/**
	 * Notifies the sketch about a display mode change. 
	 */
	protected static void notifySketch( PApplet dad ){
		try{
			Method m = dad.getClass().getMethod( "displayChanged", new Class[]{ } );
			m.invoke( dad, new Object[]{ } );
		}
		catch( Exception e ){
			
		}
	}

	/**
	 * Is opengl somehow being used in this sketch? 
	 */
	public boolean isGL(){
		return isGL;
	}
	
	/**
	 * Returns the sketch
	 * 
	 * @return The Sketch associated with this object
	 */
	public PApplet getSketch() {
		return dad;
	}
}
