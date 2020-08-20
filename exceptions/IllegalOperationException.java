package exceptions;

@SuppressWarnings("serial")
public class IllegalOperationException extends Exception{
	public IllegalOperationException(String message) {
		super("Illegal operation: " + message);
	}
}
