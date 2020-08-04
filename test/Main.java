package test;

import java.util.*;
import AST.*;

public class Main
{
	public static void main (String[] args) throws java.lang.Exception
	{
		Lexer lexer = new Lexer("i = 1; do { print i; i = i + 1; } while (i <= 5);");
		List<Token> lexres = lexer.tokenize();
		//System.out.println(lexres);
		Parser parser = new Parser(lexres);
		List<Statement> parseres = parser.parse();
		//System.out.println(parseres);
		for (Statement expr : parseres){
			expr.execute();
		}
	}
}
