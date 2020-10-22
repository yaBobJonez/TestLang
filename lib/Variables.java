package lib;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Variables {
	public static Object lock = new Object(); //Synchronization lock
	public static class Scope{
		public Scope superScope;
		public Map<String, Value> variables;
		public Scope(){
			this(null);
		} public Scope(Scope parent){
			this.superScope = parent;
			this.variables = new ConcurrentHashMap<>();
		}
	}
	public static class DataRetriever{
		public boolean found;
		public Scope scope;
	}
	public static volatile Scope currentScope;
	static {
		currentScope = new Scope();
		Variables.clear();
	}
	public static void clear(){
		currentScope.variables.clear();
	} public static void push(){
		synchronized(lock){
			currentScope = new Scope(currentScope);
		}
	} public static void pop(){
		synchronized(lock){
			if(currentScope.superScope != null) currentScope = currentScope.superScope; //TODO check everything
		}
	}
	public static Value get(String key) throws Exception{
		synchronized(lock){
			DataRetriever data = searchScope(key);
			if(data.found) return data.scope.variables.get(key);
			else return new BooleanValue(false);
		}
	} public static void set(String key, Value value){
		synchronized(lock){
			searchScope(key).scope.variables.put(key, value);
		}
	} public static void define(String key, Value value){
		synchronized(lock){
			currentScope.variables.put(key, value);
		}
	} public static void remove(String key){
		synchronized(lock){
			searchScope(key).scope.variables.remove(key);
		}
	} public static boolean exists(String key){
		synchronized(lock){
			return searchScope(key).found;
		}
	}
	public static DataRetriever searchScope(String var){
		DataRetriever res = new DataRetriever();
		Scope curr = currentScope;
		do{
			if(curr.variables.containsKey(var)){
				res.found = true;
				res.scope = curr;
				return res;
			}
		} while((curr = curr.superScope) != null);
		res.found = false;
		res.scope = currentScope;
		return res;
	}
}
