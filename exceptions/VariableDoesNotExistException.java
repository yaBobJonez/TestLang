package exceptions;

@SuppressWarnings("serial")
public class VariableDoesNotExistException extends Exception{
	public String name;
	public VariableDoesNotExistException(String message) {
		super("Variable " + message + " does not exist.");
		this.name = message;
	}
	public VariableDoesNotExistException(String message, int line, int ch) {
		super("Variable " + message + " does not exist; at line "+line + ", character "+ch + ".");
		this.name = message;
	}
}
