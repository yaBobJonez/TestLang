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
	@Override
	public Object asRaw() {
		return this.value;
	}
	@Override
	public int hashCode() {
		final int prime = 79;
		int result = 1;
		result = prime * result + (value ? 1231 : 1237);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		BooleanValue other = (BooleanValue) obj;
		if (value != other.value) return false;
		return true;
	}
	public String toString() { return this.asString(); }
}
