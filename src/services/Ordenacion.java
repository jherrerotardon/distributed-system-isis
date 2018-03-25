package services;

import javax.inject.Singleton;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import data.Proceso;

@Singleton
@Path("ordenacion")
public class Ordenacion {
	Proceso[] procesos = new Proceso[2];
	
	
	@Path("mensaje")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String mensaje(@DefaultValue("-1") @QueryParam(value="proceso") int id) {
		if (id == -1) return "NO PARAMETRO";
		
		switch (id) {
		case 0:
			
			break;
		case 1:
			
			break;
		default:
			return "ID NO VALIDO";
		}
		
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
	
	@Path("inicializar")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String inizializar() {
		procesos[0] = new Proceso();
		procesos[1] = new Proceso();
		
		procesos[0].start();
		procesos[1].start();
		
		return "OK";
	}

}
