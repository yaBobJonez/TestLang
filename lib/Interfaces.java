package lib;

import java.util.HashMap;
import java.util.Map;

import exceptions.ClassDoesNotExistException;

public class Interfaces {
	public static Map<String, InterfaceValue> interfaces;
	static{
		Interfaces.interfaces = new HashMap<>();
	}
	public static boolean exists(String name){
		return Interfaces.interfaces.containsKey(name);
	} public static InterfaceValue get(String name) throws ClassDoesNotExistException{
		if(!Interfaces.exists(name)) throw new ClassDoesNotExistException(name); //TODO InterfaceDoesNotExistException
		return Interfaces.interfaces.get(name);
	} public static void set(String name, InterfaceValue val){
		Interfaces.interfaces.put(name, val);
	}
}
