package test;

import java.util.*;
import AST.*;

public class Main
{
	public static void main (String[] args) throws java.lang.Exception
	{
		Lexer lexer = new Lexer("var1 = 1; while (var1 <= 3) { print var1; var1 = var1 + 1 } for (var2 = 0, var2 < 3, var2 = var2 + 1) print 'Hi'");
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
