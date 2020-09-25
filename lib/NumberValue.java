package lib;

public class NumberValue implements Value {
	public Number value;
	public NumberValue(Number value) {
		this.value = value;
	}
	public Number asRaw(){
		return this.value;
	}
	@Override
	public String asString() {
		return String.valueOf(this.value);
	}
	public Byte asByte() {
		return this.value.byteValue();
	}
	public short asShort() {
		return this.value.shortValue();
	}
	@Override
	public int asInteger() {
		return this.value.intValue();
	}
	public long asLong() {
		return this.value.longValue();
	}
	public float asFloat() {
		return this.value.floatValue();
	}
	@Override
	public double asDouble() {
		return this.value.doubleValue();
	}
	@Override
	public boolean asBoolean() {
		return (this.value.doubleValue() == 0.0)? false : true;
	}
	@Override
	public int hashCode() {
		return 41 * 3 + this.value.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Number other = ((NumberValue) obj).value;
		if(this.value instanceof Double || other instanceof Double) return Double.compare(this.value.doubleValue(), other.doubleValue()) == 0;
		else if(this.value instanceof Float || other instanceof Float) return Float.compare(this.value.floatValue(), other.floatValue()) == 0;
		else if(this.value instanceof Long || other instanceof Long) return Long.compare(this.value.longValue(), other.longValue()) == 0;
		else return Integer.compare(this.value.intValue(), other.intValue()) == 0;
	}
	public String toString() { return this.asString(); }
}
