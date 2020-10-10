package AST;

import java.util.Scanner;

import lib.NumberValue;
import lib.StringValue;
import lib.Variables;

public class InputStatement implements Statement {
	public String name;
	public Expression msg;
	public InputStatement(String expr, Expression msg) {
		this.name = expr;
		this.msg = msg;
	}
	@Override
	public void execute() throws Exception{
		if(this.msg != null) System.out.println(this.msg.eval());
		@SuppressWarnings("resource")
		String sc = new Scanner(System.in).nextLine();
		try{
			NumberValue value = new NumberValue(Integer.parseInt(sc));
			Variables.define(this.name, value); return;
		} catch(NumberFormatException e1){
			try{
				NumberValue value = new NumberValue(Double.parseDouble(sc));
				Variables.define(this.name, value); return;
			} catch(NumberFormatException e2){
				StringValue value = new StringValue(sc);
				Variables.define(this.name, value); return;
			}
		}
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
