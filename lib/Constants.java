package lib;

import java.util.HashMap;
import java.util.Map;

public class Constants {
	public static Map<String, Double> constants;
	static {
		constants = new HashMap<>();
		constants.put("PI", 3.1415926535);
		constants.put("GRAVITY", 9.807);
	}
}
