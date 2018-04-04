package utils;

import com.google.gson.Gson;
import data.Mensaje;

public class MensajeUtils {
	
	public static String getJsonFromMensaje(Mensaje m) {
		Gson gson = new Gson();
		String json = gson.toJson(m);
		return json;
	}

	public static Mensaje getMensajeFromJson(String json) {
		Gson gson = new Gson();
		Mensaje m = gson.fromJson(json, Mensaje.class);
		return m;
	}
}
