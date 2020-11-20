package AST;

import java.util.*;
import lib.*;

public class SwitchStatement implements Statement {
	public Expression search;
	public List<CaseValue> body;
	public Statement def;
	public SwitchStatement(Expression search, List<CaseValue> body, Statement def) {
		this.search = search;
		this.body = body;
		this.def = def;
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
	@Override
	public void execute() throws Exception {
		Value search = this.search.eval();
		for(CaseValue curr_case : this.body){
			for(Expression match : curr_case.match){
				if(match.eval().equals(search)){
					curr_case.body.execute(); return;
				}
			}
		} this.def.execute();
	}
}
