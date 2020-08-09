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
	public String toString(){
		return this.asString();
	}
}
