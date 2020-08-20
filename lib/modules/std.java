package lib.modules;

import java.util.concurrent.TimeUnit;
import exceptions.ArgumentsMismatchException;
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
			if(args.length != 1){ throw new ArgumentsMismatchException(1); }
			System.out.print(args[0].asString());
			return new BooleanValue(true);
		});
		//wait
		Functions.set("wait", args -> {
			if(args.length != 1){ throw new ArgumentsMismatchException(1); }
			TimeUnit.MILLISECONDS.sleep(args[0].asInteger());
			return new BooleanValue(true);
		});
	}
}
