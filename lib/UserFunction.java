package lib;

import java.util.*;
import AST.*;

public class UserFunction implements Function {
	public List<String> args;
	public Statement body;
	public UserFunction(List<String> args, Statement body) {
		this.args = args;
		this.body = body;
	}
	@Override
	public Value execute(Value... arguments) throws Exception{
		int size = arguments.length;
		if(size != this.args.size()) throw new Exception("Expected "+this.args.size()+" arguments.");
		try {
			Variables.push();
			for(int i = 0; i < size; i++){ Variables.set(this.getArg(i), arguments[i]); }
			this.body.execute();
			return new BooleanValue(true);
		} catch(ReturnStatement rt) {
			return rt.outcome();
		} finally { Variables.pop(); }
	} public String getArg(int index){
		if(index < 0 || index >= this.args.size()){ return null; }
		return this.args.get(index);
	}
	@Override
	public String toString() {
		return "UserFunction{args = " + this.args + "; body = " + this.body + "}";
	}
}
