package lib;

import java.util.HashMap;
import java.util.Map;

public class Constants {
	public static Map<String, Value> constants;
	static {
		constants = new HashMap<>();
		constants.put("PI", new NumberValue(3.1415926535));
		constants.put("GRAVITY", new NumberValue(9.807));
	}
}
