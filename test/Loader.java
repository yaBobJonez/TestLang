package test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Loader {
	public static String readSource(String path) throws IOException{
		InputStream stream = Loader.class.getResourceAsStream(path);
		if(stream != null) return Loader.readStream(stream);
		stream = new FileInputStream(path);
		return Loader.readStream(stream);
	} public static String readStream(InputStream str) throws IOException{
		String text = "";
		InputStreamReader sr = new InputStreamReader(str, "UTF-8");
		BufferedReader reader = new BufferedReader(sr);
		String line;
		while((line = reader.readLine()) != null){
			text += line + System.lineSeparator();
		} return text;
	}
}
