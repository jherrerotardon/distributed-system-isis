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

	private Queue<Mensaje> cola;
	private int orden;
	
	
	//Temporal hasta que sepamos hacer dispatcher
	@PostConstruct
	public void inizializar() {
		cola = new LinkedList<Mensaje>();
		orden = 0;
	}
	
	@Path("mensaje")
	@POST
	@Consumes(MediaType.TEXT_PLAIN) 
	@Produces(MediaType.TEXT_PLAIN)
	public String mensaje(String json, @QueryParam(value = "k") String k) {
		
		
		
		lc1(orden);
		Mensaje m = MensajeUtils.getMensajeFromJson(json);
		//Peticion.peticionPost("localhost", Peticion.PROPUESTA, MensajeUtils.getJsonFromMensaje(m));
		return json;

	}

	@Path("propuesta")
	@POST
	@Produces(MediaType.TEXT_PLAIN)
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
