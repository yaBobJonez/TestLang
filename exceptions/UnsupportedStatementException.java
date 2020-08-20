package exceptions;

@SuppressWarnings("serial")
public class UnsupportedStatementException extends Exception{
	public UnsupportedStatementException() {
		super();
	}
	public UnsupportedStatementException(int line, int character) {
		super("At line "+line + ", character "+character + ".");
	}
}
