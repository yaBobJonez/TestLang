package exceptions;

@SuppressWarnings("serial")
public class TypeConsumingException extends Exception{
	public TypeConsumingException(String type) {
		super("Expected " + type + " value.");
	}
}
