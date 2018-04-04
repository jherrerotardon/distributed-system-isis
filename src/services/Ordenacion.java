package services;

import java.util.LinkedList;
import java.util.Queue;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import data.Mensaje;
import data.Proceso;
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
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String mensaje(@QueryParam(value = "m") String mesg, @QueryParam(value = "m") String k) {
		
		
		
		lc1(orden);
		
		return "OK";

	}

	@Path("propuesta")
	@GET
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
