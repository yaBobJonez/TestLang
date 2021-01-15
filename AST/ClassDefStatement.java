package AST;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import lib.Classes;
import lib.FunctionValue;
import lib.InstanceValue;
import lib.MapValue;
import lib.StringValue;
import lib.UserFunction;
import lib.Value;
import lib.ClassMethod;
import lib.ClassValue;

public class ClassDefStatement implements Statement {
	public String name;
	public List<String> extendsClasses = new ArrayList<>();
	public List<AssignmentNode> fields = new ArrayList<>();
	public List<FuncDefStatement> methods = new ArrayList<>();
	public MapValue staticContainer = new MapValue();
	public List<String> privateList = new ArrayList<>();
	public ClassDefStatement(String name) {
		this.name = name;
	}
	@Override
	public void execute() throws Exception {
		ClassValue classValue = new ClassValue(this.name);
		if(!this.extendsClasses.isEmpty()){
			for(String extClassName : this.extendsClasses){
				ClassValue extClassValue = Classes.get(extClassName);
				classValue.fields.addAll(extClassValue.fields);
				classValue.methods.addAll(extClassValue.methods);
				classValue.staticContainer.array.putAll(extClassValue.staticContainer.array);
				classValue.privateList.addAll(extClassValue.privateList);
			}
		} classValue.fields.addAll(this.fields);
		classValue.methods.addAll(this.methods);
		classValue.staticContainer.array.putAll(this.staticContainer.array);
		classValue.privateList.addAll(this.privateList);
		Classes.set(this.name, classValue);
	}
	public void addField(AssignmentNode field, byte caller) throws Exception{
		this.fields.add(field);
		if(caller == 1) this.privateList.add( ((VariableNode)field.target).token.value );
	} public void addMethod(FuncDefStatement field, byte caller){
		this.methods.add(field);
		if(caller == 1) this.privateList.add(field.name);
	}
	public void addStaticField(String key, Value value, byte caller){
		this.staticContainer.set(new StringValue(key), value);
		if(caller == 1) this.privateList.add(key);
	} public void addStaticMethod(FuncDefStatement func, byte caller){
		InstanceValue dummyObj = new InstanceValue(this.name);
		dummyObj.container = this.staticContainer; //Optimization: may be better to \/ create "ClassStaticMethod" instead of dummyObj.
		this.staticContainer.set(new StringValue(func.name), new FunctionValue(new ClassMethod(func.args, func.body, dummyObj)) );
		if(caller == 1) this.privateList.add(func.name);
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
	@Override
	public String toString() {
		return "ClassStatement{name = "+this.name + "; fields = "+this.fields + "; methods = "+this.methods + "; statics = "+
				this.staticContainer + "}";
	}
}
