package lib;

public class NullValue implements Value {
	@Override
	public String asString() {
		return "null";
	}
	@Override
	public int asInteger() {
		return (Integer) null;
	}
	@Override
	public double asDouble() {
		return (Double) null;
	}
	@Override
	public boolean asBoolean() {
		return false;
	}
	@Override
	public String toString() {
		return "null";
	}
}
