package services;

import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import data.Mensaje;
import data.Proceso;
import utils.MensajeUtils;
import utils.Peticion;

@Singleton
@Path("ordenacion")
public class Ordenacion {

	private LinkedList<Mensaje> cola;
	private int orden;
	private final String localhostIPv6 = "0:0:0:0:0:0:0:1";

	@Context
	HttpServletRequest request;

	// Temporal hasta que sepamos hacer dispatcher
	@PostConstruct
	public void inizializar() {
		cola = new LinkedList<Mensaje>();
		orden = 0;
	}

	@Path("prueba")
	@GET
	// @Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public String prueba(@QueryParam(value = "k") String k) {

		System.out.println(request.getRemoteAddr());
		return request.getRemoteAddr();
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
