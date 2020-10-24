package exceptions;

@SuppressWarnings("serial")
public class ClassDoesNotExistException extends Exception{
	public String name;
	public ClassDoesNotExistException(String message) {
		super("Class " + message + " does not exist.");
		this.name = message;
	}
	public ClassDoesNotExistException(String message, int line, int ch) {
		super("Class " + message + " does not exist; at line "+line + ", character "+ch + ".");
		this.name = message;
	}
}
