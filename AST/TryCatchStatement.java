package AST;

import java.util.Map;
import java.util.Map.Entry;

public class TryCatchStatement implements Statement {
	public Statement body;
	public Map<String, Statement> catches;
	public TryCatchStatement(Statement body, Map<String, Statement> catches) {
		this.body = body;
		this.catches = catches;
	}
	@Override
	public void execute() throws Exception {
		try {
			this.body.execute();
		} catch(Throwable e){
			String got = e.getClass().getSimpleName();
			for(Entry<String, Statement> entry : this.catches.entrySet()){
				if(entry.getKey().equals(got)){
					entry.getValue().execute();
					return;
				}
			} if(this.catches.containsKey("Exception")){
				this.catches.get("Exception").execute();
				return;
			} else if(this.catches.containsKey("Error")) {
				this.catches.get("Error");
				return;
			} else throw e; //TODO throwables of any type / types
		}
	}
	@Override
	public void accept(Visitor visitor) throws Exception {
		visitor.visit(this);
	}
}
