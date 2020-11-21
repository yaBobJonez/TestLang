package AST;

import java.util.List;
import java.util.Map.Entry;

import lib.ClassMethod;
import lib.Classes;
import lib.InstanceValue;
import lib.Value;
import lib.ClassValue;

public class ObjectNode implements Expression {
	public String className;
	public List<Expression> consArgs;
	public ObjectNode(String name, List<Expression> args){
		this.className = name;
		this.consArgs = args;
	}
	@Override
	public Value eval() throws Exception {
		ClassValue classVal = Classes.get(this.className);
		InstanceValue obj = new InstanceValue(this.className);
		for(Entry<Value, Value> entry : classVal.staticContainer){
			obj.container.set(entry.getKey(), entry.getValue());
		} for(AssignmentNode asgn : classVal.fields){
			String field = ((VariableNode)asgn.target).token.value;
			obj.addField(field, asgn.eval());
		} for(FuncDefStatement method : classVal.methods){
			obj.addMethod(method.name, new ClassMethod(method.args, method.body, obj));
		} int size = this.consArgs.size();
		Value[] args = new Value[size];
		for(int i = 0; i < size; i++){
			args[i] = this.consArgs.get(i).eval();
		} obj.callConstructor(args);
		return obj;
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
	@Override
	public String toString() {
		return "ObjectNode{class = "+this.className + "; args = "+this.consArgs.toString() + "}";
	}
}
