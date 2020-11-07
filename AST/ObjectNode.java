package AST;

import java.util.List;

import lib.ClassMethod;
import lib.Classes;
import lib.InstanceValue;
import lib.Value;

public class ObjectNode implements Expression {
	public String className;
	public List<Expression> consArgs;
	public ObjectNode(String name, List<Expression> args){
		this.className = name;
		this.consArgs = args;
	}
	@Override
	public Value eval() throws Exception {
		ClassStatement classSt = Classes.get(this.className);
		InstanceValue obj = new InstanceValue(this.className);
		for(AssignmentNode asgn : classSt.fields){
			String field = ((VariableNode)asgn.target).token.value;
			obj.addField(field, asgn.eval());
		} for(FuncDefStatement method : classSt.methods){
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