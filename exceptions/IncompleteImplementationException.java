package exceptions;

@SuppressWarnings("serial")
public class IncompleteImplementationException extends Exception{
	public IncompleteImplementationException(String str, String cl) {
		super("Incomplete implementation of " + str + " in " + cl);
	}
	public IncompleteImplementationException(String str, String cl, int line, int character) {
		super("Incomplete implementation of " + str + " in " + cl + " at line "+line + ", character "+character + ".");
	}
}
