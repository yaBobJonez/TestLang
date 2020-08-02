package test;

public class Token {
	public String type;
	public String value;
	public Token(String Rtype, String Rvalue){
		this.type = Rtype; this.value = Rvalue;
	} public String toString(){
		if (this.value != null) { return this.type + ":" + this.value; }
		return this.type;
	}
}
