package AST;

import java.util.Map;
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
	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws Exception {
		Value prevVarKey = Variables.variables.containsKey(this.key) ? Variables.get(this.key) : null;
		Value prevVarValue = null;
		if(this.value != null){ prevVarValue = Variables.variables.containsKey(this.value) ? Variables.get(this.value) : null; }
		if(this.value != null){
			Iterable<Map.Entry<Value, Value>> iterator = (Iterable<Map.Entry<Value, Value>>)this.array.eval();
			for(Map.Entry<Value, Value> entry : iterator){
				Variables.set(this.key, entry.getKey());
				Variables.set(this.value, entry.getValue());
				try {
					this.body.execute();
				} catch(BreakStatement e) {
					break;
				} catch(ContinueStatement e) {
					//TODO Continue
				}
			}
		} else {
			Iterable<Value> iterator = (Iterable<Value>)this.array.eval();
			for(Value key : iterator){
				Variables.set(this.key, key);
				try {
					this.body.execute();
				} catch(BreakStatement e) {
					break;
				} catch(ContinueStatement e) {
					//TODO Continue
				}
			}
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
