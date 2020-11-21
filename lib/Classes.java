package lib;

import java.util.HashMap;
import java.util.Map;

import exceptions.ClassDoesNotExistException;

public class Classes {
	public static Map<String, ClassValue> classes;
	static{
		Classes.classes = new HashMap<>();
	}
	public static boolean exists(String name){
		return Classes.classes.containsKey(name);
	} public static ClassValue get(String name) throws ClassDoesNotExistException{
		if(!Classes.exists(name)) throw new ClassDoesNotExistException(name);
		return Classes.classes.get(name);
	} public static void set(String name, ClassValue val){
		Classes.classes.put(name, val);
	}
}
