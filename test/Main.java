package test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import AST.*;
import lib.CallStack;
import visitors.FunctionsPresetter;

public class Main
{
	public static void main (String[] args) throws Exception
	{
		//Testing purposes
		if(args.length == 0){ run("print 23 + 2;", false, false); return; }
		boolean tokens = false; boolean ast = false;
		String input = fromFile(args[0]);
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
	
	public static String fromFile(String path) throws IOException{
		return new String(Files.readAllBytes(Paths.get(path)), "UTF-8");
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
