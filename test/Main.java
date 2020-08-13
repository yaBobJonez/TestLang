package test;

import java.util.*;
import AST.*;
import visitors.FunctionsPresetter;

public class Main
{
	public static void main (String[] args) throws java.lang.Exception
	{
		Lexer lexer = new Lexer("arr = [1, 2, 3]; foreach(num in arr) print num; map = {'1':'one', '2':'two', '3':'three'}; foreach(k:v in map) print k + ' - ' + v;");
		List<Token> lexres = lexer.tokenize();
		//System.out.println(lexres);
		Parser parser = new Parser(lexres);
		Statement parseres = parser.parse();
		//System.out.println(parseres);
		parseres.accept(new FunctionsPresetter());
		parseres.execute();
	}
}
