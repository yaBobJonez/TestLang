package test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParseErrors implements Iterable<ParseError>{
	public List<ParseError> errors;
	public ParseErrors() {
		this.errors = new ArrayList<>();
	}
	public void add(Exception ex, int line){
		this.errors.add(new ParseError(line, ex));
	} public boolean hasErrors(){
		return !this.errors.isEmpty();
	}
	@Override
	public Iterator<ParseError> iterator() {
		return this.errors.iterator();
	}
	@Override
	public String toString() {
		String str = "";
		for(ParseError er : this.errors){
			str += er.toString();
		} return str;
	}
}
