package AST;

import java.util.ArrayList;
import java.util.List;

import lib.Classes;
import lib.FunctionValue;
import lib.MapValue;
import lib.StringValue;
import lib.UserFunction;
import lib.Value;
import lib.ClassValue;

public class ClassDefStatement implements Statement {
	public String name;
	public List<AssignmentNode> fields = new ArrayList<>();
	public List<FuncDefStatement> methods = new ArrayList<>();
	public MapValue staticContainer = new MapValue();
	public ClassDefStatement(String name) {
		this.name = name;
	}
	@Override
	public void execute() throws Exception {
		ClassValue classValue = new ClassValue(this.name);
		classValue.fields = this.fields;
		classValue.methods = this.methods;
		classValue.staticContainer = this.staticContainer;
		Classes.set(this.name, classValue);
	}
	public void addField(AssignmentNode field){
		this.fields.add(field);
	} public void addMethod(FuncDefStatement field){
		this.methods.add(field);
	}
	public void addStaticField(String key, Value value){
		this.staticContainer.set(new StringValue(key), value);
	} public void addStaticMethod(FuncDefStatement func){
		this.staticContainer.set(new StringValue(func.name), new FunctionValue(new UserFunction(func.args, func.body)) );
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
	@Override
	public String toString() {
		return "ClassStatement{name = "+this.name + "; fields = "+this.fields + "; methods = "+this.methods + "}";
	}
}
