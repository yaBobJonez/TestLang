package exceptions;

@SuppressWarnings("serial")
public class InvalidExpressionException extends Exception{
	public InvalidExpressionException(String str) {
		super("Invalid expression: " + str);
	}
	public InvalidExpressionException(String str, int line, int character) {
		super("Invalid expression: " + str + " at line "+line + ", character "+character + ".");
	}
}
