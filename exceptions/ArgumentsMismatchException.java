package exceptions;

@SuppressWarnings("serial")
public class ArgumentsMismatchException extends Exception{
	public ArgumentsMismatchException(int count) {
		super(String.valueOf(count) + " arguments expected.");
	}
	public ArgumentsMismatchException(int count, int line, int character) {
		super(String.valueOf(count) + " arguments expected at line "+line + ", character "+character + ".");
	}
}
