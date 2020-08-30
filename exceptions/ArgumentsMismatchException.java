package exceptions;

@SuppressWarnings("serial")
public class ArgumentsMismatchException extends Exception{
	public ArgumentsMismatchException(boolean min, int count) {
		super((min ? "Minimum " : "Maximum ") + String.valueOf(count) + " arguments expected.");
	}
	public ArgumentsMismatchException(boolean min, int count, int  provided) {
		super((min ? "Minimum " : "Maximum ") + String.valueOf(count) + " arguments expected, provided " +
		String.valueOf(provided) + ".");
	}
	public ArgumentsMismatchException(boolean min, int count, int line, int character) {
		super((min ? "Minimum " : "Maximum ") + String.valueOf(count) + " arguments expected at line "+line +
		", character "+character + ".");
	}
	public ArgumentsMismatchException(boolean min, int count, int provided, int line, int character) {
		super((min ? "Minimum " : "Maximum ") + String.valueOf(count) + " arguments expected, provided " +
		String.valueOf(provided) + " at line "+line + ", character "+character + ".");
	}
}
