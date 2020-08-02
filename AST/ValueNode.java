package AST;

import lib.*;
import test.Token;
import test.TokenList;

public class ValueNode implements Expression {
	public Token token;
	public ValueNode(Token Rvalue){
		this.token = Rvalue;
	}
	@Override
	public Value eval() {
		if(this.token.type == TokenList.TT_DOUBLE){ return new NumberValue(Double.parseDouble(this.token.value)); }
		else if(this.token.type == TokenList.TT_INT){ return new NumberValue(Double.parseDouble(this.token.value)); }
		else if(this.token.type == TokenList.TT_BOOL){ return new BooleanValue(Boolean.parseBoolean(this.token.value)); }
		else { return new StringValue(String.valueOf(this.token.value)); }
	}
	public String toString(){
		return String.valueOf(this.token);
	}
}
