package test;

import java.util.*;
import test.Token;
import test.TokenList;

public class Lexer {
	public String text;
	public char curr_char;
	public int position;
	public Lexer(String code){
		this.text = code;
		this.curr_char = (char)0;
		this.position = -1;
		this.advance();
	} public void advance(){
		this.position += 1;
		this.curr_char = (this.position < this.text.length()) ? this.text.toCharArray()[this.position] : (char)0;
	} public List<Token> tokenize(){
		List<Token> tokens = new ArrayList<Token>();
		while((this.curr_char != (char)0) && (this.position < this.text.length())){
			if(this.curr_char == ' '){ this.advance(); }
			else if("0123456789".indexOf(this.curr_char) != -1){ tokens.add(this.buildNumber()); }
			else if("\"'".indexOf(this.curr_char) != -1){ tokens.add(this.buildString(this.curr_char)); }
			else if("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(this.curr_char) != -1){ tokens.add(this.buildWord()); }
			else if("=+-*/>?:^%,.!<&|".indexOf(this.curr_char) != -1) { tokens.add(this.buildOperator()); }
			else if(this.curr_char == '@') { tokens.add(this.buildConstant()); }
			else if(this.curr_char == '(') { tokens.add(new Token(TokenList.TO_LPAR, null)); this.advance(); }
			else if(this.curr_char == ')') { tokens.add(new Token(TokenList.TO_RPAR, null)); this.advance(); }
			else if(this.curr_char == '[') { tokens.add(new Token(TokenList.TO_LBRA, null)); this.advance(); }
			else if(this.curr_char == ']') { tokens.add(new Token(TokenList.TO_RBRA, null)); this.advance(); }
			else if(this.curr_char == '{') { tokens.add(new Token(TokenList.TO_LCURL, null)); this.advance(); }
			else if(this.curr_char == '}') { tokens.add(new Token(TokenList.TO_RCURL, null)); this.advance(); }
			else if(this.curr_char == ';') { tokens.add(new Token(TokenList.TS_SEMICOLON, null)); this.advance(); }
			else if(this.curr_char == '#') { this.advance(); this.buildComment(); }
			else { System.out.println("Illegal char: "+this.curr_char); this.advance(); }
		}
		tokens.add(new Token(TokenList.TS_EOF, null));
		return tokens;
	} public Token buildNumber(){
		String number = "";
		boolean dot = false;
		while("0123456789.".indexOf(this.curr_char) != -1){
			if(this.curr_char == '.'){
				if(dot == true){ break; } else 
				{ dot = true; number += "."; }
			} else { number += this.curr_char; }
			this.advance();
		} if(dot == true){ return new Token(TokenList.TT_DOUBLE, number); }
		else { return new Token(TokenList.TT_INT, number); }
	} public Token buildWord(){
		String word = "";
		while("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_0123456789".indexOf(this.curr_char) != -1){
			word += this.curr_char;
			this.advance();
		} switch(word) {
			case "if": return new Token(TokenList.TS_IF, null);
			case "else": return new Token(TokenList.TS_ELSE, null);
			case "while": return new Token(TokenList.TS_WHILE, null);
			case "do": return new Token(TokenList.TS_DOWHILE, null);
			case "for": return new Token(TokenList.TS_FOR, null);
			case "foreach": return new Token(TokenList.TS_FOREACH, null);
			case "function": return new Token(TokenList.TS_FUNCTION, null);
			case "switch": return new Token(TokenList.TS_SWITCH, null);
			case "import": return new Token(TokenList.TS_IMPORT, null);
			case "class": return new Token(TokenList.TS_CLASS, null);
			case "break": return new Token(TokenList.TA_BREAK, null);
			case "continue": return new Token(TokenList.TA_CONTINUE, null);
			case "return": return new Token(TokenList.TA_RETURN, null);
			case "use": return new Token(TokenList.TA_USE, null);
			case "case": return new Token(TokenList.TA_CASE, null);
			case "new": return new Token(TokenList.TA_NEW, null);
			case "print": return new Token(TokenList.TI_OUT, null);
			case "input": return new Token(TokenList.TI_IN, null);
			case "try": return new Token(TokenList.TS_TRY, null);
			case "catch": return new Token(TokenList.TS_CATCH, null);
			default: return new Token(TokenList.TS_ID, word);
		}
	} public Token buildString(char starter){
		String string = "";
		this.advance();
		while(this.curr_char != (char)0){
			if(this.curr_char == starter){ this.advance(); return new Token(TokenList.TT_STRING, string); }
			else {
				string += this.curr_char; this.advance();
			}
		} return null;
	} public Token buildOperator(){
		String operator = "";
		while("=+-*/>?:^%,.!<&|".indexOf(this.curr_char) != -1){
			operator += this.curr_char; this.advance();
		} switch(operator){
			case "=": return new Token(TokenList.TS_ASSIGN, null);
			case "+=": return new Token(TokenList.TS_PLUSASSIGN, null);
			case "-=": return new Token(TokenList.TS_MINUSASSING, null);
			case "*=": return new Token(TokenList.TS_MULTIPLYASSIGN, null);
			case "/=": return new Token(TokenList.TS_DIVIDEASSIGN, null);
			case "+": return new Token(TokenList.TO_PLUS, null);
			case "-": return new Token(TokenList.TO_MINUS, null);
			case "*": return new Token(TokenList.TO_MULTIPLY, null);
			case "/": return new Token(TokenList.TO_DIVIDE, null);
			case "?": return new Token(TokenList.TS_QUESTION, null);
			case ":": return new Token(TokenList.TS_COLON, null);
			case "->": return new Token(TokenList.TS_ACCESS, null);
			case ",": return new Token(TokenList.TO_COMMA, null);
			case ".": return new Token(TokenList.TO_PERIOD, null);
			case "==": return new Token(TokenList.TL_EQUALS, null);
			case "!=": return new Token(TokenList.TL_NEQUALS, null);
			case "!": return new Token(TokenList.TL_NOT, null);
			case ">=": return new Token(TokenList.TL_EQGREATER, null);
			case "<=": return new Token(TokenList.TL_EQLESS, null);
			case ">": return new Token(TokenList.TL_GREATER, null);
			case "<": return new Token(TokenList.TL_LESS, null);
			case "&": return new Token(TokenList.TL_AND, null);
			case "|": return new Token(TokenList.TL_OR, null);
		} return null;
	} public Token buildConstant(){
		this.advance();
		Token name = this.buildWord();
		return new Token(TokenList.TT_CONST, name.value);
	} public void buildComment(){
		while(this.curr_char != '#'){
			this.advance();
		} this.advance();
	}
}