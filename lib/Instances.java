package lib;

import java.util.HashMap;
import java.util.Map;

import exceptions.ClassDoesNotExistException;

public class Instances {
	public static Map<String, InstanceValue> objects;
	static{
		Instances.objects = new HashMap<>();
	}
	public static boolean exists(String name){
		return Instances.objects.containsKey(name);
	} public static InstanceValue get(String name) throws ClassDoesNotExistException{
		if(!exists(name)) throw new ClassDoesNotExistException(name);
		return Instances.objects.get(name);
	} public static void set(String name, InstanceValue val){
		Instances.objects.put(name, val);
	}
}
