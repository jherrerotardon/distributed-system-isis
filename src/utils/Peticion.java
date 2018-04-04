package utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
	
	public static String peticionPost(String ip, String metodo, Object obj) {
		String respuesta = "";
		String servicio = "http://"+ip+":8080/PracticaObligatoriaISIS/ordenacion/";
		
		//TODO post Json
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

	public static String peticionGetWithPath(String ip, String path) {
		String respuesta = "";
		String servicio = "http://"+ip+":8080/PracticaObligatoriaISIS/";
		
		try {
			URL url = new URL(servicio + path);
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
}
