package AST;

public interface Node {
	void accept(Visitor visitor) throws Exception;
}
