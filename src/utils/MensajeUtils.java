package utils;

import com.google.gson.Gson;
import data.Mensaje;

public class MensajeUtils {
	
	public static void main(String[] args) {
		Mensaje m = new Mensaje(0, 1, 2, "estado");
		
		String json = getJsonString(m);
		System.out.println(json);
		
	}
	
	public static String getJsonString(Mensaje m) {
		Gson gson = new Gson();
		String json = gson.toJson(m);
		return json;
	}

}
