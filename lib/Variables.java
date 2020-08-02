package lib;

import java.util.HashMap;
import java.util.Map;

public class Variables {
	public static Map<String, Value> variables;
	static {
		variables = new HashMap<>();
	} public static Value get(String key) throws Exception{
		if(!Variables.variables.containsKey(key)){ throw new Exception("Not defined variable: "+key); }
		return Variables.variables.get(key);
	} public static void set(String key, Value value){
		Variables.variables.put(key, value);
	}
}
