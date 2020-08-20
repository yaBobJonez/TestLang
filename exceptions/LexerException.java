package exceptions;

@SuppressWarnings("serial")
public class LexerException extends Exception{
	public LexerException(String message) {
		super(message + ".");
	}
	public LexerException(String message, int line, int character) {
		super(message + " at line "+line + ", character "+character + ".");
	}
}
