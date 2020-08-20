package lib;

import java.util.*;
import exceptions.FunctionDoesNotExistException;

public class Functions {
	public static Map<String, Function> functions;
	public static boolean exists(String name){
		return Functions.functions.containsKey(name);
	} public static Function get(String name) throws Exception{
		if(!Functions.exists(name)){ throw new FunctionDoesNotExistException(name); }
		return Functions.functions.get(name);
	} public static void set(String name, Function function){
		Functions.functions.put(name, function);
	}
	static {
		Functions.functions = new HashMap<>();
	}
}
