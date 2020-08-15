package AST;

import java.util.*;
import lib.*;

public class SwitchStatement implements Statement {
	public Expression search;
	public List<CaseValue> body;
	public SwitchStatement(Expression search, List<CaseValue> body) {
		this.search = search;
		this.body = body;
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
	@Override
	public void execute() throws Exception {
		String search = this.search.eval().asString();
		for(CaseValue curr_case : this.body){
			if(curr_case.match.eval().asString().equals(search)){
				curr_case.body.execute(); break;
			}
		}
	}
}
