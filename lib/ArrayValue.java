package lib;

import java.util.*;

public class ArrayValue implements Value {
	public List<Value> elements;
	public ArrayValue() {
		this.elements = new ArrayList<>();
	}
	public ArrayValue(List<Value> args) {
		this.elements = new ArrayList<>();
		for(Value arg : args){ this.elements.add(arg); }
	}
	public ArrayValue(ArrayValue array) {
		this(array.elements); //MAY NOT WORK!
	}
	public Value get(int index) {
		return this.elements.get(index);
	} public void set(int index, Value value) {
		if(index < this.elements.size()) this.elements.set(index, value);
		else this.elements.add(index, value);
	}
	@Override
	public String asString() {
		return this.elements.toString();
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
	public int hashCode() {
		return 31 * 1 + Objects.hashCode(this.elements);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ArrayValue other = (ArrayValue) obj;
		return Objects.equals(this.elements, other.elements);
	}
	public String toString() {
		return this.asString();
	}
}
