package exceptions;

@SuppressWarnings("serial")
public class IllegalPropertyAccessException extends Exception{
	public String name;
	public IllegalPropertyAccessException(String message) {
		super("Cannot access " + message + " property.");
		this.name = message;
	}
	public IllegalPropertyAccessException(String message, int line, int ch) {
		super("Cannot access " + message + " property; at line "+line + ", character "+ch + ".");
		this.name = message;
	}
}
