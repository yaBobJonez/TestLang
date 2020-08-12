package test;

import java.util.*;
import AST.*;
import visitors.FunctionsPresetter;

public class Main
{
	public static void main (String[] args) throws java.lang.Exception
	{
		Lexer lexer = new Lexer("map = {0:'zero', 1:'one'}; print map; map[2] = 'two'; print map;"); //TODO arr[] — ability to append elements.
		List<Token> lexres = lexer.tokenize();
		//System.out.println(lexres);
		Parser parser = new Parser(lexres);
		Statement parseres = parser.parse();
		//System.out.println(parseres);
		parseres.accept(new FunctionsPresetter());
		parseres.execute();
	}
}
