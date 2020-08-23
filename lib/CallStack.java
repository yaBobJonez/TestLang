package lib;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

public class CallStack {
	public static Deque<Call> calls = new ConcurrentLinkedDeque<Call>();
	public static synchronized void entry(String name, Function func){
		calls.push(new Call(name, func));
	}
	public static synchronized void exit(){
		calls.pop();
	}
	public static class Call{
		String name;
		Function func;
		public Call(String name, Function func) {
			this.name = name;
			this.func = func;
		}
		@Override
		public String toString() {
			return "FrameStackCall{name = " + this.name + "; function = " + this.func + "}";
		}
		public String output() {
			return this.name + ": " + this.func;
		}
	}
}
