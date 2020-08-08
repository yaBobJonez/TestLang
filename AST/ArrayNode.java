package AST;

import lib.*;
import java.util.*;

public class ArrayNode implements Expression {
	public List<Expression> elements;
	public ArrayNode(List<Expression> elements) {
		this.elements = elements;
	}
	@Override
	public Value eval() throws Exception {
		int size = this.elements.size();
		ArrayValue array = new ArrayValue();
		for(int i = 0; i < size; i++){
			array.set(i, this.elements.get(i).eval());
		} return array;
	}
	public String toString(){
		return this.elements.toString();
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
