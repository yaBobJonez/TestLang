package lib;

public class BooleanValue implements Value {
	public boolean value;
	public BooleanValue(boolean value) {
		this.value = value;
	}
	@Override
	public String asString() {
		return String.valueOf(this.asInteger());
	}

	@Override
	public int asInteger() {
		if(this.value){ return 1; }
		return 0;
	}

	@Override
	public double asDouble() {
		return (double)this.asInteger();
	}

	@Override
	public boolean asBoolean() {
		return this.value;
	}

}
