package lib;

import java.util.*;

public class MapValue implements Value, Iterable<Map.Entry<Value, Value>> {
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
	@Override
	public Object asRaw() {
		return this.array;
	}
	@Override
	public Iterator<Map.Entry<Value, Value>> iterator(){
		return this.array.entrySet().iterator();
	}
	@Override
	public int hashCode() {
		return 37 * 5 + Objects.hashCode(this.array);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		MapValue other = (MapValue) obj;
		return Objects.equals(array, other.array);
	}
	public String toString(){
		return this.asString();
	}
}
