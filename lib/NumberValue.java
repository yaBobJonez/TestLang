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
	@Override
	public int hashCode() {
		long temp = Double.doubleToLongBits(value);
		return 41 * 3 + (int)(temp ^ (temp >>> 32));
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		NumberValue other = (NumberValue) obj;
		return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(other.value);
	}
	public String toString() { return this.asString(); }
}
