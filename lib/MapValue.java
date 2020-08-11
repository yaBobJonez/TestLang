package lib;

import java.util.*;

public class MapValue implements Value {
	public Map<Value, Value> array;
	public MapValue(Map<Value, Value> array) {
		this.array = new HashMap<>();
		this.array = array;
	}
	public MapValue() {
		this.array = new HashMap<>();
	}
	public Value get(Value key){
		return this.array.get(key);
	} public void set(Value key, Value value){
		this.array.put(key, value);
	}
	@Override
	public String asString() {
		return this.array.toString();
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
