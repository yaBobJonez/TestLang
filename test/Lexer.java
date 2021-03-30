package test;

import java.util.*;
import exceptions.LexerException;

public class Lexer {
	public String text;
	public char curr_char;
	public int position;
	public int line, character;
	public Lexer(String code){
		this.text = code;
		this.curr_char = (char)0;
		this.position = -1;
		this.line = this.character = 1;
		this.advance();
	} public void advance(){
		this.position += 1;
		this.curr_char = (this.position < this.text.length()) ? this.text.toCharArray()[this.position] : (char)0;
		if(this.curr_char == '\n'){ this.line += 1; this.character = 1; }
		else this.character += 1;
	} public List<Token> tokenize() throws Exception{
		List<Token> tokens = new ArrayList<Token>();
		while((this.curr_char != (char)0) && (this.position < this.text.length())){
			if((this.curr_char == ' ') || (this.curr_char == '\n')){ this.advance(); }
			else if("0123456789".indexOf(this.curr_char) != -1){ tokens.add(this.buildNumber()); }
			else if("\"'".indexOf(this.curr_char) != -1){ tokens.add(this.buildString(this.curr_char)); }
			else if("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(this.curr_char) != -1){ tokens.add(this.buildWord()); }
			else if("=+-*/>?:^%,.!<&|".indexOf(this.curr_char) != -1) { tokens.add(this.buildOperator()); }
			else if(this.curr_char == '@') { tokens.add(this.buildConstant()); }
			else if(this.curr_char == '`') { tokens.add(this.buildExtended()); }
			else if(this.curr_char == '(') { tokens.add(new Token(TokenList.TO_LPAR, null)); this.advance(); }
			else if(this.curr_char == ')') { tokens.add(new Token(TokenList.TO_RPAR, null)); this.advance(); }
			else if(this.curr_char == '[') { tokens.add(new Token(TokenList.TO_LBRA, null)); this.advance(); }
			else if(this.curr_char == ']') { tokens.add(new Token(TokenList.TO_RBRA, null)); this.advance(); }
			else if(this.curr_char == '{') { tokens.add(new Token(TokenList.TO_LCURL, null)); this.advance(); }
			else if(this.curr_char == '}') { tokens.add(new Token(TokenList.TO_RCURL, null)); this.advance(); }
			else if(this.curr_char == ';') { tokens.add(new Token(TokenList.TS_SEMICOLON, null)); this.advance(); }
			else if(this.curr_char == '#') { this.advance(); this.buildComment(); }
			else { char last = this.curr_char; this.advance(); throw new LexerException("Unrecognized character: " + last, this.line, this.character); }
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
		} if(dot == true){ return new Token(TokenList.TT_DOUBLE, number, this.line, this.character); }
		else { return new Token(TokenList.TT_INT, number, this.line, this.character); }
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
			case "true": return new Token(TokenList.TT_BOOL, "true");
			case "false": return new Token(TokenList.TT_BOOL, "false");
			case "null": return new Token(TokenList.TT_NULL, null);
			case "inf": return new Token(TokenList.TT_INF, null);
			case "nan": return new Token(TokenList.TT_NAN, null);
			case "function": return new Token(TokenList.TS_FUNCTION, null);
			case "switch": return new Token(TokenList.TS_SWITCH, null);
			case "include": return new Token(TokenList.TS_INCLUDE, null);
			case "class": return new Token(TokenList.TS_CLASS, null);
			case "extends": return new Token(TokenList.TS_EXTENDS, null);
			case "interface": return new Token(TokenList.TS_INTERFACE, null);
			case "implements": return new Token(TokenList.TS_IMPLEMENTS, null);
			case "break": return new Token(TokenList.TA_BREAK, null);
			case "continue": return new Token(TokenList.TA_CONTINUE, null);
			case "return": return new Token(TokenList.TA_RETURN, null);
			case "static": return new Token(TokenList.TA_STATIC, null);
			case "public": return new Token(TokenList.TA_PUBLIC, null);
			case "private": return new Token(TokenList.TA_PRIVATE, null);
			case "use": return new Token(TokenList.TA_USE, null);
			case "case": return new Token(TokenList.TA_CASE, null);
			case "default": return new Token(TokenList.TA_DEFAULT, null);
			case "new": return new Token(TokenList.TA_NEW, null);
			case "in": return new Token(TokenList.TA_IN, null);
			case "print": return new Token(TokenList.TI_OUT, null);
			case "input": return new Token(TokenList.TI_IN, null);
			case "try": return new Token(TokenList.TS_TRY, null);
			case "catch": return new Token(TokenList.TS_CATCH, null);
			case "raise": return new Token(TokenList.TS_RAISE, null);
			default: return new Token(TokenList.TS_ID, word, this.line, this.character);
		}
	} public Token buildString(char starter){
		String string = "";
		this.advance();
		while(this.curr_char != (char)0){
			if(this.curr_char == starter){ this.advance(); return new Token(TokenList.TT_STRING, string); }
			else if(this.curr_char == '\\'){
				this.advance();
				switch(this.curr_char){
					case 'n': string += '\n'; this.advance(); break;
					case 't': string += '\t'; this.advance(); break;
					case 'b': string += '\b'; this.advance(); break;
					case 'f': string += '\f'; this.advance(); break;
					case 'u': {
						this.advance(); String code = "";
						for(int i = 0; i < 4; i++){
							code += this.curr_char;
							this.advance();
						} string += (char)Integer.parseInt(code, 16);
						break;
					}
					case '\\': string += '\\'; this.advance(); break;
				}
			} else {
				string += this.curr_char; this.advance();
			}
		} return null;
	} public Token buildExtended(){
		this.advance();
		String string = "";
		while(this.curr_char != (char)0){
			if(this.curr_char == '`'){ this.advance(); return new Token(TokenList.TS_ID, string); }
			else { string += this.curr_char; this.advance(); }
		} return null;
	} public Token buildOperator() throws LexerException{
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
			case "%": return new Token(TokenList.TO_MODULO, null);
			case "^": return new Token(TokenList.TO_POWER, null);
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
		} throw new LexerException("Unrecognized operator: "+operator, this.line, this.character);
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