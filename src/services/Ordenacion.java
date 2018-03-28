package services;

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

@Singleton
@Path("ordenacion")
public class Ordenacion {
	Proceso[] procesos = new Proceso[2];

	//Temporal hasta que sepamos hacer dispatcher
	private Queue<Mensaje> cola;
	private int orden;
	
	@PostConstruct
	public void inizializar() {
		procesos[0] = new Proceso();
		procesos[1] = new Proceso();

		procesos[0].start();
		procesos[1].start();
	}
	
	@Path("mensaje")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String mensaje(@QueryParam(value = "m") String mesg, @QueryParam(value = "m") String k) {
//		if (id <= -1) {
//			return "NO PARAMETRO";
//		} else if (id >=2){
//			return "ID NO VALIDA";
//		} else {
//			
//		}
		
		orden = orden + 1;
		
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
	

}
