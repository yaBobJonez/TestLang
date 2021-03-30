package AST;

import java.util.ArrayList;
import java.util.List;

import exceptions.ImplementationMismatchException;
import exceptions.IncompleteImplementationException;
import lib.Classes;
import lib.FunctionValue;
import lib.InstanceValue;
import lib.InterfaceValue;
import lib.Interfaces;
import lib.MapValue;
import lib.StringValue;
import lib.Value;
import lib.ClassMethod;
import lib.ClassValue;

public class ClassDefStatement implements Statement {
	public String name;
	public List<String> implementInterfaces = new ArrayList<>(); //TODO
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
		} if(!this.implementInterfaces.isEmpty()){
			for(String implInterfaceName : this.implementInterfaces){ //Use extreme care with statics and privates, they are tricky.
				InterfaceValue implInterfaceValue = Interfaces.get(implInterfaceName);
				for(String asgn : implInterfaceValue.fields){
					boolean found = false;
					if(implInterfaceValue.privateList.contains(asgn))
						if(!this.privateList.contains(asgn)) throw new ImplementationMismatchException(implInterfaceName, this.name);
						else found = true;
					else if(this.privateList.contains(asgn)) throw new ImplementationMismatchException(implInterfaceName, this.name);
					if(implInterfaceValue.staticList.contains(asgn))
						if(!this.staticContainer.array.containsKey(new StringValue(asgn)))
							throw new ImplementationMismatchException(implInterfaceName, this.name);
						else found = true;
					else if(this.staticContainer.array.containsKey(new StringValue(asgn)))
						throw new ImplementationMismatchException(implInterfaceName, this.name);
					if(!found) for(AssignmentNode intAsgn : this.fields)
						if(((VariableNode)intAsgn.target).token.value.equals(asgn)){ found = true; break; }
					if(!found) throw new IncompleteImplementationException(implInterfaceName, this.name);
				} for(FuncDefStatement fn : implInterfaceValue.methods){
					boolean found = false;
					if(implInterfaceValue.privateList.contains(fn.name))
						if(!this.privateList.contains(fn.name)) throw new ImplementationMismatchException(implInterfaceName, this.name);
						else found = true;
					else if(this.privateList.contains(fn.name)) throw new ImplementationMismatchException(implInterfaceName, this.name);
					if(implInterfaceValue.staticList.contains(fn.name))
						if(!this.staticContainer.array.containsKey(new StringValue(fn.name)))
							throw new ImplementationMismatchException(implInterfaceName, this.name);
						else found = true;
					else if(this.staticContainer.array.containsKey(new StringValue(fn.name)))
						throw new ImplementationMismatchException(implInterfaceName, this.name);
					if(!found) for(FuncDefStatement iterFn : this.methods) if(iterFn.name.equals(fn.name)){ found = true; break; }
					if(!found) throw new IncompleteImplementationException(implInterfaceName, this.name);
				}
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
