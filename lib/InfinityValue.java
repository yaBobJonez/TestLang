package lib;

public class InfinityValue implements Value {	
	public boolean negative;
	public InfinityValue(boolean sign) {
		this.negative = sign;
	}
	@Override
	public String asString() {
		if(this.negative) return "-infinity";
		else return "infinity";
	}
	@Override
	public int asInteger() {
		if(this.negative) return Integer.MIN_VALUE;
		else return Integer.MAX_VALUE;
	}
	@Override
	public double asDouble() {
		if(this.negative) return Double.NEGATIVE_INFINITY;
		else return Double.POSITIVE_INFINITY;
	}
	@Override
	public boolean asBoolean() {
		return true;
	}
	@Override
	public Object asRaw() {
		return this.asDouble();
	}
	//TODO hash, equals
	@Override
	public String toString() {
		return this.asString();
	}
}
