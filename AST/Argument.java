package AST;

public class Argument {
	public String name;
	public Expression value;
	public Argument(String name) {
		this.name = name;
		this.value = null;
	}
	public Argument(String name, Expression value) {
		this.name = name;
		this.value = value;
	}
	@Override
	public String toString() {
		String str = "Argument{name = " + this.name;
		if(this.value != null) str += "; value = " + this.value;
		return str + "}";
	}
}
