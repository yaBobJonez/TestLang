package lib;

public class NanValue implements Value {
	@Override
	public String asString() {
		return "nan";
	}
	@Override
	public int asInteger() {
		return Integer.MIN_VALUE;
	}
	@Override
	public double asDouble() {
		return Double.NaN;
	}
	@Override
	public boolean asBoolean() {
		return false;
	}
	@Override
	public String toString() {
		return "nan";
	}
}
