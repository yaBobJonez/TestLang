package lib;

public class StringValue implements Value {
	public String value;
	public StringValue(String value) {
		this.value = value;
	}
	@Override
	public String asString() {
		return this.value;
	}

	@Override
	public int asInteger() {
		try { return Integer.parseInt(this.value); }
		catch(Exception $e) { return 0; }
	}

	@Override
	public double asDouble() {
		try { return Double.parseDouble(this.value); }
		catch(Exception $e) { return 0.0; }
	}

	@Override
	public boolean asBoolean() {
		try { return Boolean.parseBoolean(this.value); }
		catch(Exception $e) { return false; }
	}
	
	public String toString() { return this.value; }
}
