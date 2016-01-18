package rsacryptographer;

import java.math.BigInteger;

public final class CryptoUtility {

	private final static int CHAR_DIGITS = 3;

	public static BigInteger parseValue(String message) {
		String valueString = "1";
		for (char c : message.toCharArray())
			valueString = valueString.concat(String.format("%0" + CHAR_DIGITS
					+ "d", (int) c));
		return new BigInteger(valueString);
	}

	public static String toMessage(BigInteger value) {
		String valueString = value.toString().substring(1);
		String message = "";
		while (valueString.length() > CHAR_DIGITS - 1) {
			message += (char) Integer.parseInt(valueString.substring(0,
					CHAR_DIGITS));
			valueString = valueString.substring(CHAR_DIGITS);
		}
		if (valueString.length() > 0)
			message.concat("[" + valueString + "]");
		return message;
	}
}
