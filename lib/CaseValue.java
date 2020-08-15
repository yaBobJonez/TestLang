package lib;

import AST.*;

public class CaseValue implements Value {
	public Expression match;
	public Statement body;
	public CaseValue(Expression match, Statement body) {
		this.match = match;
		this.body = body;
	}
	@Override
	public String asString() {
		return "Case{value = " + this.match + "; body = " + this.body + "}";
	}
	@Override
	public int asInteger() {
		return 1;
	}
	@Override
	public double asDouble() {
		return 1.0;
	}
	@Override
	public boolean asBoolean() {
		return true;
	}
	public String toString() {
		return this.asString();
	}
}
