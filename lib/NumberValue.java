package lib;

public class NumberValue implements Value {
	public double value;
	public NumberValue(double value) {
		this.value = value;
	}
	@Override
	public String asString() {
		return String.valueOf(this.value);
	}

	@Override
	public int asInteger() {
		return (int)this.value;
	}

	@Override
	public double asDouble() {
		return this.value;
	}

	@Override
	public boolean asBoolean() {
		if(this.value == 0.0){ return true; }
		return false;
	}
	
	public String toString() { return this.asString(); }
}
