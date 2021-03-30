package exceptions;

@SuppressWarnings("serial")
public class ImplementationMismatchException extends Exception{
	public ImplementationMismatchException(String str, String cl) {
		super("Implementation type mismatch of " + str + " in " + cl);
	}
	public ImplementationMismatchException(String str, String cl, int line, int character) {
		super("Implementation type mismatch of " + str + " in " + cl + " at line "+line + ", character "+character + ".");
	}
}
