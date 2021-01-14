package lib;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class InstanceValue implements Value {
	public String className;
	public MapValue container;
	public ClassMethod constructor; //TODO multiple constructors
	public List<String> privateList = new ArrayList<>();
	public InstanceValue(String className) {
		this.className = className;
		this.container = new MapValue();
	}
	public void addField(String key, Value value){
		this.container.set(new StringValue(key), value);
	} public void addMethod(String key, ClassMethod value){
		this.container.set(new StringValue(key), new FunctionValue(value));
		if(key.equals("constructor")) this.constructor = value;
	} public Value get(Value name) throws Exception{
		//if(caller == (byte)1 && privateList.contains(name.asString())) throw new IllegalPropertyAccessException(name.asString());
		return this.container.get(name);
	} public void set(Value key, Value value) throws Exception{
		//if(caller == (byte)1 && privateList.contains(key.asString())) throw new IllegalPropertyAccessException(key.asString());
		this.container.set(key, value);
	}
	public void callConstructor(Value[] args) throws Exception{
		if(this.constructor != null) this.constructor.execute(args);
	}
	@Override
	public Object asRaw() {
		return null;
	}
	@Override
	public String asString() {
		return "Object("+this.className+")";
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
        return 23 * 5 + Objects.hash(this.className, this.container);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        InstanceValue other = (InstanceValue) obj;
        return Objects.equals(this.className, other.className) && Objects.equals(this.container, other.container);
    }
    @Override
    public String toString(){
    	String str = "InstanceValue{name = "+this.className + "; container = "+this.container;
    	if(this.constructor != null) str += "; constructor = "+this.constructor;
    	return str + "}";
    }
}
