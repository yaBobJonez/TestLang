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
		try {
			this.body.execute();
			return new BooleanValue(false);
		} catch(ReturnStatement rt) {
			return rt.outcome();
		}
	} public String getArg(int index){
		if(index < 0 || index >= this.args.size()){ return null; }
		return this.args.get(index);
	}
	@Override
	public String toString() {
		return "UserFunction{args = " + this.args + "; body = " + this.body + "}";
	}
}
