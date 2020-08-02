package lib;

public class IntegerValue implements Value {
	public int value;
	public IntegerValue(int value) {
		this.value = value;
	}
	@Override
	public String asString() {
		return String.valueOf(this.value);
	}

	@Override
	public int asInteger() {
		return this.value;
	}

	@Override
	public double asDouble() {
		return (double)this.value;
	}

	@Override
	public boolean asBoolean() {
		if(this.value == 0){ return false; }
		return true;
	}

}
