package lib;

import java.util.HashMap;
import java.util.Map;

import AST.ClassStatement;
import exceptions.ClassDoesNotExistException;

public class Classes {
	public static Map<String, ClassStatement> classes;
	static{
		Classes.classes = new HashMap<>();
	}
	public static boolean exists(String name){
		return Classes.classes.containsKey(name);
	} public static ClassStatement get(String name) throws ClassDoesNotExistException{
		if(!Classes.exists(name)) throw new ClassDoesNotExistException(name);
		return Classes.classes.get(name);
	} public static void set(String name, ClassStatement val){
		Classes.classes.put(name, val);
	}
}
