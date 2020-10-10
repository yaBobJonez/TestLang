package exceptions;

@SuppressWarnings("serial")
public class PropertyDoesNotExistException extends Exception{
	public String name;
	public PropertyDoesNotExistException(String message) {
		super("Property " + message + " does not exist.");
		this.name = message;
	}
	public PropertyDoesNotExistException(String message, int line, int ch) {
		super("Property " + message + " does not exist; at line "+line + ", character "+ch + ".");
		this.name = message;
	}
}
