package lib;

import java.util.concurrent.*;
import java.util.*;

public class Functions {
	public static Map<String, Function> functions;
	public static boolean exists(String name){
		return Functions.functions.containsKey(name);
	} public static Function get(String name) throws Exception{
		if(!Functions.exists(name)){ throw new Exception("Function does not exist."); }
		return Functions.functions.get(name);
	} public static void set(String name, Function function){
		Functions.functions.put(name, function);
	}
	static {
		Functions.functions = new HashMap<>();
		//TODO basic functions
		Functions.set("printm", args -> {
			for(Value arg : args){ System.out.println(arg); }
			return new BooleanValue(true);
		});
		Functions.set("Arthur", args -> {
			if(args.length != 1){ throw new Exception("One int argument expected."); }
			TimeUnit.MILLISECONDS.sleep(args[0].asInteger());
			return new BooleanValue(true);
		});
	}
}
