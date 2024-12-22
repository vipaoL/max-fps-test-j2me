import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

public class TestCanvas extends GameCanvas {
	private static final int TEST_LAUNCH_DELAY = 3000, TEST_DURATION = 3000;
	private Thread thread = null;
	private Graphics g = null;
	private int w, h;
	private int frameCounter;
	private long launchCountdownEndTime, testStartTime, testEndTime;
	private boolean stopped;
	private boolean color;

	protected TestCanvas() {
		super(false);
		setFullScreenMode(true);
		launchCountdownEndTime = System.currentTimeMillis() + TEST_LAUNCH_DELAY;
	}

	public void start() {
		new Thread(new Runnable() {
			public void run() {
				// if there is a previous test thread running, stop it
				stop();
				stopped = false;
				thread = Thread.currentThread();

				w = getWidth();
				h = getHeight();
				g = null;

				// show the countdown timer once after the app was opened
				int c;
				while ((c = (int) (launchCountdownEndTime - System.currentTimeMillis())) > 0 && !stopped) {
					drawCountdown(c);
				}

				// refresh the screen for three seconds and count frames
				frameCounter = 0;
				testStartTime = System.currentTimeMillis();
				testEndTime = testStartTime + TEST_DURATION;
				while (System.currentTimeMillis() < testEndTime && !stopped) {
					refillScreen();
					frameCounter++;
				}
				// save the actual end time
				testEndTime = System.currentTimeMillis();

				// show the result
				showSummary();
			}
		}).start();
	}

	public void stop() {
		// "ask" the thread to stop
		stopped = true;
		try {
			if (thread != null) {
				// wait for the thread to stop
				thread.join();
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

	protected void sizeChanged(int w, int h) {
		if (w != this.w || h != this.h) {
			this.w = w;
			this.h = h;
			g = null; // Graphics instance should be invalidated if the screen size was changed
			start(); // restart the test
		}
	}

	private void refillScreen() {
		Graphics g = getG();
		g.setColor(color ? 0x000055 : 0x000000);
		g.fillRect(0, 0, w, h);
		color = !color;
		flushGraphics();
	}

	private void showSummary() {
		Graphics g = getG();
		g.setColor(0x000000);
		g.fillRect(0, 0, w, h);

		double frameTime = (double) (testEndTime - testStartTime) / frameCounter;
		g.setColor(0xffffff);
		g.drawString("FPS: " + round(1000 / frameTime), w/2, h/2, Graphics.BOTTOM | Graphics.HCENTER);
		g.drawString("(" + round(frameTime) + " ms)", w/2, h/2, Graphics.TOP | Graphics.HCENTER);
		flushGraphics();
	}

	private void drawCountdown(int c) {
		Graphics g = getG();
		g.setColor(0);
		g.fillRect(0, 0, w, h);

		g.setColor(0xffffff);
		g.drawString(String.valueOf(c/1000 + 1), w/2, h/2 - g.getFont().getHeight() / 2, Graphics.TOP | Graphics.HCENTER);
		flushGraphics();
	}

	protected void keyReleased(int keyCode) {
		start();
	}

	protected void pointerReleased(int x, int y) {
		start();
	}

	// round to two decimal places
	private double round(double d) {
		return (Math.floor(d * 100 + 0.5)) / 100;
	}

	private Graphics getG() {
		if (g == null) {
			g = getGraphics();
		}
		return g;
	}
}