package AST;

import java.util.ArrayList;
import java.util.List;
import lib.InterfaceValue;
import lib.Interfaces;

public class InterfaceDefStatement implements Statement {
	public String name;
	public List<String> fields = new ArrayList<>();
	public List<FuncDefStatement> methods = new ArrayList<>();
	public List<String> staticList = new ArrayList<>();
	public List<String> privateList = new ArrayList<>();
	public InterfaceDefStatement(String name) {
		this.name = name;
	}
	@Override
	public void execute() throws Exception {
		InterfaceValue interfaceValue = new InterfaceValue(this.name);
		interfaceValue.fields.addAll(this.fields);
		interfaceValue.methods.addAll(this.methods);
		interfaceValue.staticList.addAll(this.staticList);
		interfaceValue.privateList.addAll(this.privateList);
		Interfaces.set(this.name, interfaceValue);
	}
	public void addField(String field, boolean isStatic, byte caller) throws Exception{
		this.fields.add(field);
		if(isStatic) this.staticList.add(field);
		if(caller == 1) this.privateList.add(field);
	} public void addMethod(FuncDefStatement field, boolean isStatic, byte caller){
		this.methods.add(field);
		if(isStatic) this.staticList.add(field.name);
		if(caller == 1) this.privateList.add(field.name);
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
	@Override
	public String toString() {
		return "InterfaceStatement{name = "+this.name + "; fields = "+this.fields + "; methods = "+this.methods + "}";
	}
}
