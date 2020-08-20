package exceptions;

@SuppressWarnings("serial")
public class InvalidExpressionException extends Exception{
	public InvalidExpressionException() {
		super();
	}
	public InvalidExpressionException(int line, int character) {
		super("At line "+line + ", character "+character + ".");
	}
}
