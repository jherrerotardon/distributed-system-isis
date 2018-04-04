package data;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import utils.MensajeUtils;
import utils.Peticion;

@Singleton
@Path("proceso")
public class Proceso extends Thread {
	private String[] ipProcesos;
	private Queue<Mensaje> cola;
	private int orden;

	public Proceso() {
		cola = new LinkedList<Mensaje>();
		orden = 0;
	}
	
	public static void main(String[] args) {
		Mensaje m = new Mensaje(12, 25, 1, "estado");
		
		
		String json = Peticion.peticionPost("localhost", Peticion.MENSAJE, MensajeUtils.getJsonFromMensaje(m));
		System.out.println("retorno" + json);
	}
	
	@Override
	public void run() {

		for (int i = 100; i < 100; i++) {
			//Mensaje m = new Mensaje(this.getId(), orden);

			//bMulticast(ipProcesos, m, Peticion.MENSAJE);

			try {
				Thread.sleep((long) ((Math.random() * 0.5 + 1.0) * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	
	private void bMulticast(String[] ipProcesos, Mensaje m, String metodo) {

		for (String ip : ipProcesos) {

			Peticion.peticionGet(ip, metodo, "m=" + m.getContenido() + "&" + "k=" + m.getId());

			try {
				Thread.sleep((long) ((Math.random() * 0.3 + 0.2) * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
