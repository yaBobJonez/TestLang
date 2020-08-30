package AST;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Arguments implements Iterable<Argument>{
	public List<Argument> args;
	public int requiredNum;
	public Arguments() {
		this.args = new ArrayList<>();
		this.requiredNum = 0;
	} public void addRequired(String name){
		this.args.add(new Argument(name));
		this.requiredNum += 1;
	} public void addOptional(String name, Expression expr){
		this.args.add(new Argument(name, expr));
	}
	@Override
	public Iterator<Argument> iterator() {
		return this.args.iterator();
	}
	@Override
	public String toString() {
		String str = "{";
		for(Argument arg : this.args) str += arg + ", ";
		return str + "}";
	}
}
