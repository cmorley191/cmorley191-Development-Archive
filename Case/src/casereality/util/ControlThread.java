package casereality.util;

import java.util.Scanner;

public class ControlThread extends Thread {

	public ControlThread(UpdateThread.UpdateFunction updateFunction) {
		super(new Runnable() {

			@Override
			public void run() {
				Scanner scanner = new Scanner(System.in);
				while (updateFunction.enabled()) {
					scanner.nextLine();
					updateFunction.update();
				}
				System.out.println("Ended");
				scanner.close();
			}
			
		});
	}

	public ControlThread(UpdateThread.UpdateFunction updateFunction, long a, long b) {
		this(updateFunction);
	}
}
