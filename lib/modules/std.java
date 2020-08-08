package lib.modules;

import java.util.concurrent.TimeUnit;

import lib.*;

public class std implements Module {
	@Override
	public void initialize() {
		//print Multiple
		Functions.set("printM", args -> {
			for(Value arg : args){ System.out.println(arg); }
			return new BooleanValue(true);
		});
		//print Append
		Functions.set("printA", args -> {
			if(args.length != 1){ throw new Exception("One argument expected."); }
			System.out.print(args[0].asString());
			return new BooleanValue(true);
		});
		//wait
		Functions.set("wait", args -> {
			if(args.length != 1){ throw new Exception("One int argument expected."); }
			TimeUnit.MILLISECONDS.sleep(args[0].asInteger());
			return new BooleanValue(true);
		});
	}
}
