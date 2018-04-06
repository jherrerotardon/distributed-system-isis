package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.json.JsonObject;

public class Peticion {
	
	public static final String MENSAJE = "mensaje";
	public static final String PROPUESTA = "propuesta";
	public static final String ACUERDO = "acuerdo";
	
	public static String peticionGet(String ip, String metodo) {
		String respuesta = "";
		String servicio = "http://"+ip+":8080/PracticaObligatoriaISIS/ordenacion/";
		
		try {
			URL url = new URL(servicio + metodo);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			while ((output = br.readLine()) != null) {
				respuesta += output;
				respuesta += '\n';
			}
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return respuesta;
	}
	
	//GET con argumentos
	public static String peticionGet(String ip, String metodo, String args) {
		String respuesta = "";
		String servicio = "http://"+ip+":8080/PracticaObligatoriaISIS/ordenacion/";
		
		try {
			URL url = new URL(servicio + metodo + '?' + args);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			while ((output = br.readLine()) != null) {
				respuesta += output;
				respuesta += '\n';
			}
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return respuesta;
	}
	
	//POST mediante json
	public static String peticionPost(String ip, String metodo, String jsonMensaje) {
		String respuesta = "";
		String servicio = "http://"+ip+":8080/PracticaObligatoriaISIS/ordenacion/";
		
		try {
			URL url = new URL(servicio + metodo);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			
			System.out.println("envio" + jsonMensaje);
			
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			
			OutputStream os = conn.getOutputStream();
			os.write(jsonMensaje.getBytes());
            os.close();
            
			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}
			
			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			String output;
			while ((output = br.readLine()) != null) {
				respuesta += output;
				respuesta += '\n';
			}
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return respuesta;
	}

}
