package lib;

public interface Value {
	Object asRaw();
	String asString();
	int asInteger();
	double asDouble();
	boolean asBoolean();
}
