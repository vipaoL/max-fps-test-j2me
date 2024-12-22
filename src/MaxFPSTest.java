import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class MaxFPSTest extends MIDlet {
	private TestCanvas canvas;
	private boolean started = false;

	public MaxFPSTest() {
		canvas = new TestCanvas();
	}

	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException { }

	protected void pauseApp() { }

	protected void startApp() throws MIDletStateChangeException {
		if (!started) {
			Display.getDisplay(this).setCurrent(canvas);
			canvas.start();
			started = true;
		}
	}

}
