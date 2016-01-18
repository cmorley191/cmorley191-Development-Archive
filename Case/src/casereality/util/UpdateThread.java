package casereality.util;

public class UpdateThread extends Thread {

	public UpdateThread(UpdateFunction updateFunction, long updateInterval,
			long updateCountHalt) {
		super(new Runnable() {
			long updateCount = 0;

			@Override
			public void run() {
				long lastUpdateTime = System.currentTimeMillis();
				while (updateFunction.enabled()
						&& updateCount < updateCountHalt) {
					long currentTime = System.currentTimeMillis();
					if (currentTime - lastUpdateTime < updateInterval)
						continue;
					updateFunction.update();
					updateCount++;
					lastUpdateTime = System.currentTimeMillis();
				}
				System.out.println("Ended");
			}
		});
	}

	public static abstract class UpdateFunction {
		public abstract void update();

		public abstract boolean enabled();
	}
}
