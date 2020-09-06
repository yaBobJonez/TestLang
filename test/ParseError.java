package test;

public class ParseError {
	public int line;
	public Exception exception;
	public ParseError(int line, Exception exception) {
		this.line = line;
		this.exception = exception;
	}
	@Override
	public String toString() {
		return "ParseError{line = " + this.line + "; msg = " + this.exception.getMessage() + "}";
	}
}
