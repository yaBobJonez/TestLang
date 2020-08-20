package exceptions;

@SuppressWarnings("serial")
public class FunctionDoesNotExistException extends Exception{
	public String name;
	public FunctionDoesNotExistException(String message) {
		super("Variable " + message + " does not exist.");
		this.name = message;
	}
	public FunctionDoesNotExistException(String message, int line, int ch) {
		super("Variable " + message + " does not exist; at line "+line + ", character "+ch + ".");
		this.name = message;
	}
}
