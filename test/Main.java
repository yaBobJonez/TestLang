package test;

import java.util.*;
import AST.*;
import lib.CallStack;
import visitors.FunctionsPresetter;

public class Main
{
	public static void main (String[] args) throws Exception
	{
		Options options = new Options();
		//Testing purposes
		if(args.length == 0){ run("print 'Now you can specify more than one module to load!';", options); return; }
		String input = Loader.readSource(args[0]);
		for(int i = 1; i < args.length; i++){
			switch(args[i]){
				case "-t":
				case "-tokens": options.tokens = true; break;
				case "-a":
				case "-ast": options.ast = true; break;
				default: throw new RuntimeException("Unknown argument.");
			}
		} run(input, options);
	}
	public static void run(String input, Options options) throws Exception{
		Lexer lexer = new Lexer(input);
		List<Token> lexres = lexer.tokenize();
		if(options.tokens) System.out.println(lexres);
		Parser parser = new Parser(lexres);
		Statement parseres = parser.parse();
		if(options.ast) System.out.println(parseres);
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
	public static class Options{
		public boolean tokens, ast;
		public Options(){
			this.tokens = false;
			this.ast = false;
		}
	}
}
