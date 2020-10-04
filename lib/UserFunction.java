package lib;

import AST.*;
import exceptions.ArgumentsMismatchException;

public class UserFunction implements Function {
	public Arguments args;
	public Statement body;
	public UserFunction(Arguments args, Statement body) {
		this.args = args;
		this.body = body;
	}
	@Override
	public Value execute(Value... arguments) throws Exception{
		int givenNum = arguments.length;
		int requiredNum = this.args.requiredNum;
		if(givenNum < requiredNum) throw new ArgumentsMismatchException(true, requiredNum, givenNum);
		int totalNum = this.args.args.size();
		if(givenNum > totalNum) throw new ArgumentsMismatchException(false, totalNum, givenNum);
		try {
			Variables.push();
			for(int i = 0; i < givenNum; i++){ Variables.define(this.getArg(i), arguments[i]); }
			for(int i = givenNum; i < totalNum;	i++){
				Argument arg = this.args.args.get(i);
				Variables.define(arg.name, arg.value.eval());
			}
			this.body.execute();
			return new BooleanValue(true);
		} catch(ReturnStatement rt) {
			return rt.outcome();
		} finally { Variables.pop(); }
	} public String getArg(int index){
		if(index < 0 || index >= this.args.args.size()){ return null; }
		return this.args.args.get(index).name;
	}
	@Override
	public String toString() {
		return "UserFunction{args = " + this.args + "; body = " + this.body + "}";
	}
}
