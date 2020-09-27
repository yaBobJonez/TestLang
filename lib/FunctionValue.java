package lib;

public class FunctionValue implements Value {
	public Function value;
	public FunctionValue(Function value) {
		this.value = value;
	}
	@Override
	public String asString() {
		return this.value.toString();
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
		return this.value;
	}
	@Override
	public int hashCode() {
		return 59 * 1 + ((value == null) ? 0 : value.hashCode());
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		FunctionValue other = (FunctionValue) obj;
		if (value == null) {
			if (other.value != null) return false;
		} else if (!value.equals(other.value)) return false;
		return true;
	}
	public String toString(){
		return this.asString();
	}
}
