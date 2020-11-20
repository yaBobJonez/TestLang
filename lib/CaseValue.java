package lib;

import java.util.List;

import AST.*;

public class CaseValue implements Value {
	public List<Expression> match;
	public Statement body;
	public CaseValue(List<Expression> match, Statement body) {
		this.match = match;
		this.body = body;
	}
	@Override
	public String asString() {
		return "Case{value = " + this.match.toString() + "; body = " + this.body + "}";
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
	@Override
	public Object asRaw() {
		return this.match;
	}
	public String toString() {
		return this.asString();
	}
}
