package test;

import java.util.*;
import AST.*;
import lib.CallStack;
import visitors.FunctionsPresetter;

public class Main
{
	public static void main (String[] args) throws Exception
	{
		//Testing purposes
		if(args.length == 0){ run("a = 1; b = 'hi'; function test(b){ a = 2; print a; print b; } print a; print b; test('well'); print a; print b;", false, false); return; }
		boolean tokens = false; boolean ast = false;
		String input = Loader.readSource(args[0]);
		for(int i = 1; i < args.length; i++){
			switch(args[i]){
				case "-t":
				case "-tokens": tokens = true; break;
				case "-a":
				case "-ast": ast = true; break;
				default: throw new RuntimeException("Unknown argument.");
			}
		} run(input, tokens, ast);
	}
	public static void run(String input, boolean tokens, boolean ast) throws Exception{
		Lexer lexer = new Lexer(input);
		List<Token> lexres = lexer.tokenize();
		if(tokens) System.out.println(lexres);
		Parser parser = new Parser(lexres);
		Statement parseres = parser.parse();
		if(ast) System.out.println(parseres);
		if(parser.parseErrors.hasErrors()){
			System.out.println(parser.parseErrors);
			System.exit(1);
		}
		parseres.accept(new FunctionsPresetter());
		try {
			parseres.execute();
		} catch(Exception e) {
			handleNegative(Thread.currentThread(), e);
		}
	}
	public static void handleNegative(Thread thread, Throwable throwable){
		System.err.print(throwable.getMessage() + " in thread " + thread.getName() + ".\nStacktrace:\n");
		for(CallStack.Call call : CallStack.calls){
			System.err.print("at " + call.output() + "\n");
		} //throwable.printStackTrace(); //For testing purposes.
	}
}
