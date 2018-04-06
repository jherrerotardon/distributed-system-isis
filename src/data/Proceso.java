package data;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import utils.MensajeUtils;
import utils.Peticion;

@Singleton
@Path("proceso")
public class Proceso extends Thread {
	private String[] ipProcesos;
	private LinkedList<Mensaje> cola;
	private int orden;
	private final String localhostIPv6 = "0:0:0:0:0:0:0:1";
	@Context HttpServletRequest request;

	public Proceso() {
		cola = new LinkedList<Mensaje>();
		orden = 0;
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
	
	@Path("mensaje")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String mensaje(@QueryParam(value = "m") String m ,@QueryParam(value = "k") String k) {
		String ip;
		
		lc1(orden);
		//Recuperacion y modificación del mensaje antes de guardarlo en la cola
		Mensaje mensajeCola = new Mensaje (m, k, orden, 0, Mensaje.PROVISIONAL);
		cola.push(mensajeCola);
		
		//Reconversion de ipv6 a ipv4 cuando viene de localhost 
		ip = request.getRemoteAddr().equals(localhostIPv6) ? "localhost" : request.getRemoteAddr();
		
		System.out.println("[Mensaje/" + ip + "]: " + " m=" + m + " k=" + k);
		
		Peticion.peticionGet(ip, Peticion.PROPUESTA, "k=" + k + "&" + "orden=" + orden);
		return "OK";
	}

	@Path("propuesta")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String propuesta(@QueryParam(value = "k") String k ,@QueryParam(value = "orden") int orden) {

		return "OK";
	}

	@Path("acuerdo")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String acuerdo(@QueryParam(value = "k") String k ,@QueryParam(value = "orden")int ordenj) {
		
		//int ordenMensaje
		//ordenMensaje = (ordenMensaje > ordenj) ? ordenMensaje : ordenj;
		lc2(orden, ordenj);
		
		
		return "OK";
	}

	private void lc1(int orden) {
		orden += 1;
	}

	private void lc2(int orden, int ordenj) {
		if (orden > ordenj) {
			orden += 1;
		} else {
			orden = ordenj + 1;
		}
	}
	
}
