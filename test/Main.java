package test;

import java.util.*;
import AST.*;

public class Main
{
	public static void main (String[] args) throws java.lang.Exception
	{
		Lexer lexer = new Lexer("word = 2 + 2; word2 = 6; print word");
		List<Token> lexres = lexer.tokenize();
		//System.out.println(lexres);
		Parser parser = new Parser(lexres);
		List<Statement> parseres = parser.parse();
		for (Statement expr : parseres){
			//System.out.println(expr);
			expr.execute();
		}
	}
}
