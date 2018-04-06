package data;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Singleton
@Path("proceso")
public class Proceso extends Thread {
	
	private final String localhostIPv6 = "0:0:0:0:0:0:0:1";
	protected DatosProceso datos;
	protected Proceso hilo;
	@Context HttpServletRequest request;
	
	public Proceso (DatosProceso datos)	{
		this.datos = datos;	
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
	
	
	
	@Path("mensaje")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String mensaje(@QueryParam(value = "m") String m ,@QueryParam(value = "k") String k) {
//		String ip;
//		
//		lc1(datos.getOrden());
//		//Recuperacion y modificación del mensaje antes de guardarlo en la cola
//		Mensaje mensajeCola = new Mensaje (m, k, datos.getOrden(), 0, Mensaje.PROVISIONAL);
//		datos.getCola().push(mensajeCola);
//		
//		//Reconversion de ipv6 a ipv4 cuando viene de localhost 
//		ip = request.getRemoteAddr().equals(localhostIPv6) ? "localhost" : request.getRemoteAddr();
//		
//		System.out.println("[Mensaje/" + ip + "]: " + " m=" + m + " k=" + k);
//		
//		Peticion.peticionGet(ip, Peticion.PROPUESTA, "k=" + k + "&" + "orden=" + datos.getOrden());
//		return "OK";
		return m;
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
		lc2(datos.getOrden(), ordenj);
		
		
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
