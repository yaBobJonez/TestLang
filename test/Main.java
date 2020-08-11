package test;

import java.util.*;
import AST.*;
import visitors.FunctionsPresetter;

public class Main
{
	public static void main (String[] args) throws java.lang.Exception
	{
		Lexer lexer = new Lexer("arr1 = {'test':'Hello world!', 2:'GOOD', true:'false'}; print arr1['test']; print arr1[2]; print arr1[true];");
		List<Token> lexres = lexer.tokenize();
		//System.out.println(lexres);
		Parser parser = new Parser(lexres);
		Statement parseres = parser.parse();
		//System.out.println(parseres);
		parseres.accept(new FunctionsPresetter());
		parseres.execute();
	}
}
