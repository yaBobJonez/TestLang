package AST;

import java.util.Map;

import exceptions.TypeConsumingException;
import lib.*;

public class ForeachStatement implements Statement {
	public String key, value;
	public Expression array;
	public Statement body;
	public ForeachStatement(String key, String value, Expression array, Statement body) {
		this.key = key;
		this.value = value;
		this.array = array;
		this.body = body;
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
	@Override
	public void execute() throws Exception {
		Value prevVarKey = Variables.exists(this.key) ? Variables.get(this.key) : null;
		Value prevVarValue = null;
		if(this.value != null){ prevVarValue = Variables.exists(this.value) ? Variables.get(this.value) : null; }
		if(this.value != null){ //key : value
			Value container = this.array.eval();
			if(container instanceof StringValue){
				int i = 0;
				for(char c : container.asString().toCharArray()){
					Variables.define(this.key, new NumberValue(i++));
					Variables.define(this.value, new StringValue(String.valueOf(c)) );
					try {
						this.body.execute();
					} catch(BreakStatement e) {
						break;
					} catch(ContinueStatement e) {}
				}
			} else if(container instanceof ArrayValue){
				int i = 0;
				for(Value v : (ArrayValue)container){
					Variables.define(this.key, new NumberValue(i++));
					Variables.define(this.value, v);
					try {
						this.body.execute();
					} catch(BreakStatement e) {
						break;
					} catch(ContinueStatement e) {}
				}
			} else if(container instanceof MapValue){
				for(Map.Entry<Value, Value> entry : (MapValue)container){
					Variables.define(this.key, entry.getKey());
					Variables.define(this.value, entry.getValue());
					try {
						this.body.execute();
					} catch(BreakStatement e) {
						break;
					} catch(ContinueStatement e) {}
				}
			} else throw new TypeConsumingException("iterable value");
		} else { //value
			Value container = this.array.eval();
			if(container instanceof StringValue){
				for(char c : container.asString().toCharArray()){
					Variables.define(this.key, new StringValue(String.valueOf(c)) );
					try {
						this.body.execute();
					} catch(BreakStatement e) {
						break;
					} catch(ContinueStatement e) {}
				}
			} else if(container instanceof ArrayValue){
				for(Value v : (ArrayValue)container){
					Variables.define(this.key, v);
					try {
						this.body.execute();
					} catch(BreakStatement e) {
						break;
					} catch(ContinueStatement e) {}
				}
			} else if(container instanceof MapValue){
				for(Map.Entry<Value, Value> entry : (MapValue)container){
					Variables.define(this.key, entry.getValue());
					try {
						this.body.execute();
					} catch(BreakStatement e) {
						break;
					} catch(ContinueStatement e) {}
				}
			} else throw new TypeConsumingException("iterable value");
		}
		if(prevVarKey != null) Variables.set(this.key, prevVarKey);
		if(prevVarValue != null) Variables.set(this.value, prevVarValue);
	}
	@Override
	public String toString() {
		String str = "Foreach{key = " + this.key;
		if(this.value != null) str += "; value = " + this.value;
		return str + "; array = " + this.array + "; actions = " + this.body;
	}
}
