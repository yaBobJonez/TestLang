package lib;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import AST.FuncDefStatement;

public class InterfaceValue implements Value {
	public String name;
	public List<String> fields = new ArrayList<>();
	public List<FuncDefStatement> methods = new ArrayList<>();
	public List<String> staticList = new ArrayList<>();
	public List<String> privateList = new ArrayList<>();
	public InterfaceValue(String name) {
		this.name = name;
	}
	@Override
	public Object asRaw() {
		return null;
	}
	@Override
	public String asString() {
		return "Interface("+this.name+")";
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
        return 89 * 5 + Objects.hash(this.name, this.fields, this.methods);
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        InterfaceValue other = (InterfaceValue) obj;
        return Objects.equals(this.name, other.name) && Objects.equals(this.fields, other.fields) &&
        		Objects.equals(this.methods, other.methods);
    }
    @Override
    public String toString(){
    	return "InterfaceValue{name = "+this.name + "; fields = "+this.fields + "; methods = "+this.methods + "}";
    }
}
