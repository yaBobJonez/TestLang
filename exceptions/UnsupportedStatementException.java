package exceptions;

@SuppressWarnings("serial")
public class UnsupportedStatementException extends Exception{
	public UnsupportedStatementException(String str) {
		super("Unsupported statement: " + str);
	}
	public UnsupportedStatementException(String str, int line, int character) {
		super("Unsupported statement: " + str + " at line "+line + ", character "+character + ".");
	}
}
