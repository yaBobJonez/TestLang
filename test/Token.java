package test;

public class Token {
	public String type;
	public String value;
	public Integer line, character;
	public Token(String Rtype, String Rvalue){
		this.type = Rtype; this.value = Rvalue;
	}
	public Token(String type, String value, int line, int character) {
		this.type = type;
		this.value = value;
		this.line = line;
		this.character = character;
	}
	public String toString(){
		String str = "";
		if ((this.line != null) | (this.character != null)) str += "[" + this.line + ", " + this.character + "] ";
		if (this.value != null) str += this.type + ":" + this.value;
		else str += this.type;
		return str;
	}
}
