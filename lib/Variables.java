package lib;

import java.util.HashMap;
import java.util.Map;

public class Variables {
	public static Map<String, Double> variables;
	static {
		variables = new HashMap<>();
	} public static double get(String key) throws Exception{
		if(!Variables.variables.containsKey(key)){ throw new Exception("Not defined variable: "+key); }
		return Variables.variables.get(key);
	} public static void set(String key, double value){
		Variables.variables.put(key, value);
	}
}
