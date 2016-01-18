package rsacryptographer;

import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

import rsacryptographer.KeyPairUtility.KeyPair;

public final class Demo {

	public static void main(String[] args) {
		System.out.println("Generating Alice's Keypair");
		KeyPair aliceKeyPair = KeyPairUtility
				.generateKeyPair(new Random(), 1024);
		System.out.println("Generating Bob's Keypair");
		KeyPair bobKeyPair = KeyPairUtility.generateKeyPair(new Random(), 1024);
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter a message to send from Alice to Bob: ");
		String text = scanner.nextLine();
		BigInteger message = CryptoUtility.parseValue(text);
		System.out.println("Signing with Alice's Private Key");
		message = KeyPairUtility.crypt(message, aliceKeyPair.privateKey);
		System.out.println("Packaging with Bob's Public Key");
		message = KeyPairUtility.crypt(message, bobKeyPair.publicKey);
		System.out.println("Sending: " + CryptoUtility.toMessage(message));
		System.out.println();
		System.out.println("Received.");
		System.out.println("Extracting with Bob's Private Key");
		message = KeyPairUtility.crypt(message, bobKeyPair.privateKey);
		System.out.println("Verifying with Alice's Public Key");
		message = KeyPairUtility.crypt(message, aliceKeyPair.publicKey);
		System.out.println("Received Message:");
		System.out.println(CryptoUtility.toMessage(message));
		scanner.close();
	}
}
