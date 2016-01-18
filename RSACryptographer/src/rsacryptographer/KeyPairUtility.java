package rsacryptographer;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Random;

public final class KeyPairUtility {

	/**
	 * Generates a random public-private {@link KeyPair} of the specified bit
	 * length.
	 * 
	 * @param random
	 *            the randomness provider to be used in generation
	 * @param bitLength
	 *            the required bit length of the keys
	 * @return a {@code KeyPair} containing a randomly-generated public and
	 *         private key
	 */
	public static KeyPair generateKeyPair(Random random, int bitLength) {
		int pBitLength = bitLength / 2;
		int qBitLength = bitLength / 2;
		if (bitLength % 2 == 1)
			if (Math.random() < 0.5)
				pBitLength++;
			else
				qBitLength++;
		// System.out.println("Generating p");
		BigInteger p = BigInteger.probablePrime(pBitLength, random);
		// System.out.println("Generating q");
		BigInteger q = BigInteger.probablePrime(qBitLength, random);
		// System.out.println("Calculating regulator");
		BigInteger regulator = p.multiply(q);
		// System.out.println("Calculating euler regulator");
		BigInteger eulerN = regulator
				.subtract(p.add(q.subtract(BigInteger.ONE)));
		// System.out.println("Calculating public key");
		BigInteger publicKey;
		while (true) {
			do {
				publicKey = new BigInteger(eulerN.bitLength(), random);
			} while (publicKey.compareTo(eulerN) >= 0);

			BigInteger publicKeyCheck = publicKey;
			BigInteger eulerNCheck = eulerN;
			BigInteger temp;
			while (!eulerNCheck.equals(BigInteger.ZERO)) {
				temp = publicKeyCheck;
				publicKeyCheck = eulerNCheck;
				eulerNCheck = temp.remainder(eulerNCheck);
			}
			if (publicKeyCheck.equals(BigInteger.ONE))
				break;
		}
		// System.out.println("Calculating private key");
		BigInteger privateKey = BigInteger.ZERO;
		BigInteger newPrivateKey = BigInteger.ONE;
		BigInteger eulerNCheck = eulerN;
		BigInteger newEulerNCheck = publicKey;
		while (!newEulerNCheck.equals(BigInteger.ZERO)) {
			BigInteger quotient = eulerNCheck.divide(newEulerNCheck);

			BigInteger tempPrivate = privateKey;
			privateKey = newPrivateKey;
			newPrivateKey = tempPrivate.subtract(quotient
					.multiply(newPrivateKey));

			BigInteger tempEuler = eulerNCheck;
			eulerNCheck = newEulerNCheck;
			newEulerNCheck = tempEuler.subtract(quotient
					.multiply(newEulerNCheck));
		}
		if (privateKey.compareTo(BigInteger.ZERO) < 0)
			privateKey = privateKey.add(eulerN);
		return new KeyPair(regulator, privateKey, publicKey);
	}

	/**
	 * Represents the integer 2 in {@code BigInteger} form.
	 */
	private final static BigInteger TWO = BigInteger.ONE.add(BigInteger.ONE);

	/**
	 * Encodes or decodes the specified message with the specified key.
	 * 
	 * @param message
	 *            the message to be encoded or decoded
	 * @param key
	 *            the key to be used to encode or decode the message
	 * @return the encoded or decoded message
	 */
	public static BigInteger crypt(BigInteger message, KeyPair.Key key) {
		BigInteger cryption = BigInteger.ONE;
		message = message.mod(key.regulator);
		BigInteger workingKey = key.key;
		while (workingKey.compareTo(BigInteger.ZERO) > 0) {
			if (workingKey.mod(TWO).equals(BigInteger.ONE))
				cryption = cryption.multiply(message).mod(key.regulator);
			workingKey = workingKey.shiftRight(1);
			message = message.multiply(message).mod(key.regulator);
		}
		return cryption;
	}

	/**
	 * Represents a pair of public and private keys.
	 * 
	 * @author Charlie Morley
	 *
	 */
	public static final class KeyPair implements Serializable {

		private static final long serialVersionUID = 6652254599239051306L;

		/**
		 * The publicly distributable key.
		 */
		public final Key privateKey;

		/**
		 * The key in the key pair to be kept private.
		 */
		public final Key publicKey;

		/**
		 * Creates a key pair with the specified components.
		 * 
		 * @param regulator
		 * @param privateKey
		 * @param publicKey
		 */
		private KeyPair(BigInteger regulator, BigInteger privateKey,
				BigInteger publicKey) {
			this.privateKey = new Key(regulator, privateKey);
			this.publicKey = new Key(regulator, publicKey);
		}

		public static final class Key {

			public final BigInteger regulator;
			public final BigInteger key;

			public Key(BigInteger regulator, BigInteger key) {
				this.regulator = regulator;
				this.key = key;
			}
		}
	}
}
