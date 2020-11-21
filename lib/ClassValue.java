package lib;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import AST.AssignmentNode;
import AST.FuncDefStatement;

public class ClassValue implements Value {
	public String className;
	public MapValue staticContainer = new MapValue();
	public List<AssignmentNode> fields = new ArrayList<>();
	public List<FuncDefStatement> methods = new ArrayList<>();
	public ClassValue(String className) {
		this.className = className;
	}
	public Value get(Value name){
		return this.staticContainer.get(name);
	} public void set(Value key, Value value){
		this.staticContainer.set(key, value);
	}
	@Override
	public Object asRaw() {
		return null;
	}
	@Override
	public String asString() {
		return "Class("+this.className+")";
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
        return 37 * 3 + Objects.hash(this.className, this.fields, this.methods, this.staticContainer);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        ClassValue other = (ClassValue) obj;
        return Objects.equals(this.className, other.className) && Objects.equals(this.fields, other.fields) && Objects.equals(
        		this.staticContainer, other.staticContainer) && Objects.equals(this.methods, other.methods);
    }
    @Override
    public String toString(){
    	return "ClassValue{name = "+this.className + "; fields = "+this.fields + "; methods = "+this.methods +
    			"; staticContainer = "+this.staticContainer + "}";
    }
}
