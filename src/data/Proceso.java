package data;

import java.util.LinkedList;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import utils.Peticion;

@Singleton
@Path("proceso")
public class Proceso extends Thread {
	
	private final String localhostIPv6 = "0:0:0:0:0:0:0:1";
	
	private String[] ipServidores;
	private LinkedList<Mensaje> cola;
	private int orden;
	private int numProceso;
	
	@Context HttpServletRequest request;
	
	public Proceso ()	{
		this.cola = new LinkedList<>();
		this.orden = 0;
	}
			
	@Override
	public void run() {

//		for (int i = 100; i < 100; i++) {
//			Mensaje m = new Mensaje(this.getId(), orden);
//
//			bMulticast(ipProcesos, m, Peticion.MENSAJE);
//
//			try {
//				Thread.sleep((long) ((Math.random() * 0.5 + 1.0) * 1000));
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
		System.err.println("Proceso: " + numProceso + "run inicio");
		for (int i = 0; i < 100; i++) {
			System.err.println("Proceso: " + numProceso + "run " + i);
			try {
				Thread.sleep((long)2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.err.println("Proceso: " + numProceso + "run fin");
	}
	
	@Path("inicializar")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String inicializar(@QueryParam(value = "proceso") int numProceso, @QueryParam(value = "proceso") String ips) {
		this.numProceso = numProceso;
		
		if(ipServidores!= null) {
			ipServidores = ips.split("|");			
		}
		this.start();
		return "OK p: " + this.numProceso;
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
