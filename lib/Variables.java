package lib;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Variables {
	public static Stack<Map<String, Value>> localvars;
	public static Map<String, Value> variables;
	static {
		localvars = new Stack<>();
		variables = new HashMap<>();
	}
	public static void push(){
		localvars.push(new HashMap<>(variables));
	} public static void pop(){
		variables = localvars.pop();
	}
	public static Value get(String key) throws Exception{
		if(!Variables.variables.containsKey(key)){ throw new Exception("Not defined variable: "+key); }
		return Variables.variables.get(key);
	} public static void set(String key, Value value){
		Variables.variables.put(key, value);
	}
}
