package lib;

import java.util.Objects;

import exceptions.PropertyDoesNotExistException;

public class StringValue implements Value {
	public String value;
	public StringValue(String value) {
		this.value = value;
	}
	@Override
	public String asString() {
		return this.value;
	}
	public Value access(Value name) throws PropertyDoesNotExistException{
		switch(name.asString()){
			case "length": return new NumberValue(this.value.length());
			default: throw new PropertyDoesNotExistException(name.asString());
		}
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
	@Override
	public Object asRaw() {
		return this.value;
	}
	@Override
	public int hashCode() {
		return 23 * 7 + Objects.hashCode(this.value);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		StringValue other = (StringValue) obj;
		return Objects.equals(value, other.value);
	}
	public String toString() { return this.value; }
}
