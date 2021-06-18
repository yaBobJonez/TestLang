package lib.modules;

import java.util.concurrent.TimeUnit;

import AST.Arguments;
import AST.FuncDefStatement;
import AST.OutputStatement;
import AST.ReturnStatement;
import AST.Statement;
import AST.ValueNode;
import AST.Visitor;
import exceptions.ArgumentsMismatchException;
import lib.*;
import lib.ClassValue;

public class std implements Module {
	@Override
	public void initialize() {
		//print Multiple
		Functions.set("printM", args -> {
			for(Value arg : args){ System.out.println(arg); }
			return new BooleanValue(true);
		});
		//print Append
		Functions.set("printA", args -> {
			if(args.length != 1){ throw new ArgumentsMismatchException(false, 1); }
			System.out.print(args[0].asString());
			return new BooleanValue(true);
		});
		//wait
		Functions.set("wait", args -> {
			if(args.length != 1){ throw new ArgumentsMismatchException(false, 1); }
			TimeUnit.MILLISECONDS.sleep(args[0].asInteger());
			return new BooleanValue(true);
		});
		ClassValue classTestClass0 = new ClassValue("TestClass0");
		classTestClass0.methods.add(new FuncDefStatement("func0", new Arguments(), new Statement(){
			@Override public void accept(Visitor visitor) throws Exception{}
			@Override public void execute() throws Exception {
				new OutputStatement(new ValueNode("Test completed.")).execute();
				new ReturnStatement(new ValueNode("hh")).execute();
			}
		})); Classes.set("TestClass0", classTestClass0);
	}
}
