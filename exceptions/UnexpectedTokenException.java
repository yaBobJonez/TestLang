package exceptions;

import test.Token;

@SuppressWarnings("serial")
public class UnexpectedTokenException extends Exception{
	public UnexpectedTokenException(String expected) {
		super("Expected " + expected + " token.");
	}
	public UnexpectedTokenException(String expected, Token got) {
		super("Expected " + expected + " token instead of " + got.toString() + ".");
	}
	public UnexpectedTokenException(String message, int line, int character) {
		super("Expected " + message + " token at line "+line + ", character "+character + ".");
	}
}
