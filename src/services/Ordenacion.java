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
	
	@Context HttpServletRequest request;
	
	
	//Temporal hasta que sepamos hacer dispatcher
	@PostConstruct
	public void inizializar() {
		cola = new LinkedList<Mensaje>();
		orden = 0;
	}
	
	@Path("prueba")
	@GET
	//@Consumes(MediaType.APPLICATION_JSON) 
	@Produces(MediaType.APPLICATION_JSON)
	public String prueba(@QueryParam(value = "k") String k) {
		
		System.out.println(request.getRemoteAddr());
		return request.getRemoteAddr();
	}
	
	@Path("mensaje")
	@POST
	@Consumes(MediaType.APPLICATION_JSON) 
	@Produces(MediaType.APPLICATION_JSON)
	public String mensaje(String json ,@QueryParam(value = "k") String k) {
		String ip;
		
		lc1(orden);
		//Recuperacion y modificación del mensaje antes de guardarlo en la cola
		Mensaje mensajeRecibido = MensajeUtils.getMensajeFromJson(json);
		mensajeRecibido.setOrden(orden);
		mensajeRecibido.setNumPeticiones(0);
		mensajeRecibido.setEstado(Mensaje.PROVISIONAL);
		
		cola.push(mensajeRecibido);
		
		//Reconversion de ipv6 a ipv4 cuando viene de localhost 
		ip = request.getRemoteAddr().equals(localhostIPv6) ? "localhost" : request.getRemoteAddr();
		
		System.out.println("[Mensaje/" + ip + "]: " + " json: " + json);
		
		Peticion.peticionPost(ip, Peticion.PROPUESTA, MensajeUtils.getJsonFromMensaje(mensajeRecibido));
		return json;
	}

	@Path("propuesta")
	@POST
	@Consumes(MediaType.APPLICATION_JSON) 
	@Produces(MediaType.APPLICATION_JSON)
	public String propuesta() {
		

		return "OK";
	}

	@Path("acuerdo")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String acuerdo() {

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
