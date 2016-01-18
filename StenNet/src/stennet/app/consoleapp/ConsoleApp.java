package stennet.app.consoleapp;

import java.util.Scanner;

import stennet.neural.HopfieldNetwork;

/**
 * Console program that allows demonstration and use of the HopfieldNetwork -
 * training and recollection.
 * 
 * @author Charlie Morley
 *
 */
public final class ConsoleApp {

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter network size: ");
		HopfieldNetwork network = new HopfieldNetwork(scanner.nextInt());
		System.out.println("Enter number of training patterns: ");
		int trainingPatterns = scanner.nextInt();
		for (int pattern = 1; pattern <= trainingPatterns; pattern++) {
			System.out.println("Enter training pattern " + pattern + ": ");
			boolean[] patternValues = new boolean[network.getNodeCount()];
			for (int i = 0; i < patternValues.length; i++)
				patternValues[i] = scanner.nextInt() == 1;
			network.trainPattern(patternValues);
		}
		while (true) {
			System.out.println("Enter a reference pattern: ");
			boolean[] pattern = new boolean[network.getNodeCount()];
			for (int i = 0; i < pattern.length; i++)
				pattern[i] = scanner.nextInt() == 1;
			scanner.nextLine();
			System.out.println("Finding nearest pattern...");
			boolean[] result = network.getNearestPattern(pattern);
			System.out.println("Nearest pattern: ");
			for (boolean value : result)
				System.out.print(((value) ? 1 : 0) + " ");
			System.out.println();
			System.out.println("Would you like to enter another pattern?");
			String response = scanner.nextLine().trim();
			if (!(response.equalsIgnoreCase("yes") || response
					.equalsIgnoreCase("y")))
				break;
		}
		scanner.close();
	}

}
