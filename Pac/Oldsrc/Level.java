package pacman.abstracts;

import java.awt.Component;
import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public abstract class Level extends JPanel implements Runnable {
	protected boolean[][] legalSpaces = new boolean[28][36];

	public enum CoordinateSystem {
		Real(1), Grid8(8);

		private int gridScale;

		CoordinateSystem(int gridScale) {
			this.gridScale = gridScale;
		}

		public int getGridScale() {
			return gridScale;
		}
	}

	public Level() {
		setLayout(null); // FlowLayout does not allow for custom JComponents
		timeUntilNextModeSwitch = getNextModeSwitchTime();
	}

	protected abstract void switchMode();

	protected abstract long getNextModeSwitchTime();

	private long timeSinceLastModeSwitch = 0;
	private long timeUntilNextModeSwitch = Long.MAX_VALUE;

	private void processTime(long processingTime) {
		timeSinceLastModeSwitch += processingTime;
		if (timeSinceLastModeSwitch >= timeUntilNextModeSwitch) {
			switchMode();
			timeSinceLastModeSwitch = 0;
			timeUntilNextModeSwitch = getNextModeSwitchTime();
		}
	}

	private final long repaintPeriod = 20; // minimum milliseconds before
											// repainting
	private final long processPeriod = 5; // minimum milliseconds before
											// reprocessing
	private final long maxProcessPeriod = 50; // maximum milliseconds to process
	private final boolean enableFPSCounter = false;
	private final long FPSPeriod = 5000; // minimum milliseconds before
											// outputting fps

	@SuppressWarnings("unused")
	@Override
	public final void run() {
		long lastRepaintTime = System.currentTimeMillis();
		long lastProcessTime = lastRepaintTime;
		long lastFPSTime = lastRepaintTime;
		long processCount = 0;
		long repaintCount = 0;
		while (true) {
			long currentTime = System.currentTimeMillis();

			// process sprites
			if (currentTime - lastProcessTime >= processPeriod) {
				processCount++;

				long processingTime = currentTime - lastProcessTime;
				if (processingTime > maxProcessPeriod)
					processingTime = maxProcessPeriod;

				// allow level to process
				processTime(processingTime);

				// find all relevant components
				ArrayList<Sprite> sprites = new ArrayList<Sprite>();
				for (Component component : this.getComponents())
					if (component instanceof Sprite)
						sprites.add((Sprite) component);

				// process movement
				for (int i = 0; i < sprites.size(); i++)
					sprites.get(i).processMovement(processingTime);

				lastProcessTime = currentTime;
			}

			// repaint
			if (currentTime - lastRepaintTime >= repaintPeriod) {
				repaintCount++;

				repaint();

				lastRepaintTime = currentTime;
			}

			// process FPS
			if (enableFPSCounter && currentTime - lastFPSTime >= FPSPeriod) {
				double timeElapsed = ((double) currentTime - (double) lastFPSTime) / 1000.0;
				double processesPS = processCount / timeElapsed;
				double repaintsPS = repaintCount / timeElapsed;

				processCount = 0;
				repaintCount = 0;

				System.out.println((long) processesPS
						+ " processes per second, " + (long) repaintsPS
						+ " repaints PS");

				lastFPSTime = currentTime;
			}
		}
	}

}
