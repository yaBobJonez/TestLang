package AST;

import java.util.*;
import lib.MapValue;
import lib.Value;

public class MapNode implements Expression {
	public Map<Expression, Expression> elements;
	public MapNode(Map<Expression, Expression> elements) {
		this.elements = elements;
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
	@Override
	public Value eval() throws Exception {
		MapValue array = new MapValue();
		for(Expression key : this.elements.keySet()){
			array.set(key.eval(), this.elements.get(key).eval());
		} return array;
	}
	@Override
	public String toString() {
		return this.elements.toString();
	}
}
