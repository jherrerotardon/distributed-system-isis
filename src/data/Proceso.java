package data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

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

	private final String LOCALHOSTIPV6 = "0:0:0:0:0:0:0:1";
	private final char OFFSETASCII = 64;
	private final int NPROCESOS = 2;

	private String[] ipServidores;
	private List<Mensaje> cola;
	private int orden;
	private int idProceso;
	private Mensaje mensaje;
	private Semaphore semaforoPreparados;

	@Context
	HttpServletRequest request;

	public Proceso() {
		this.cola = new ArrayList<>();
		this.orden = 0;
		this.semaforoPreparados = new Semaphore(0);
	}

	@Override
	public void run() {

		for (int i = 0; i < 5; i++) {
			String idMensaje = (char) (OFFSETASCII + idProceso) + "" + i;
			mensaje = new Mensaje(idMensaje, idProceso, orden);

			bMulticast(mensaje, Peticion.MENSAJE);

			try {
				Thread.sleep((long) ((Math.random() * 0.5 + 1.0) * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}
	
	@Path("saludo")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String saludo() {
		return "HOLA";
	}
	
	@Path("preparado")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String preparado() {
		semaforoPreparados.release();
		return "OK";
	}

	@Path("inicializar")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String inicializar(@QueryParam(value = "idproceso") int idProceso,
			@QueryParam(value = "ips") String ips) {
		this.idProceso = idProceso;
		if (ips != null) {
			ipServidores = ips.split("\\*");
			for (String ip : ipServidores) {
				System.out.println(ip);
			}

		}
		try {
			semaforoPreparados.acquire(NPROCESOS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.start();
		return "OK p: " + this.idProceso;
	}

	@Path("mensaje")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String mensaje(@QueryParam(value = "proceso") int numProceso, @QueryParam(value = "m") String m,
			@QueryParam(value = "k") String k) {
		String ip;

		lc1();
		// Recuperacion y modificación del mensaje antes de guardarlo en la cola
		String ordenMensaje = orden + "." + idProceso;
		Mensaje mensajeCola = new Mensaje(m, k, ordenMensaje, 0, Mensaje.PROVISIONAL);
		cola.add(mensajeCola);

		// Reconversion de ipv6 a ipv4 cuando viene de localhost
		ip = request.getRemoteAddr().equals(LOCALHOSTIPV6) ? "localhost" : request.getRemoteAddr();

		System.out.println("[Mensaje/" + ip + "]: " + " m=" + m + " k=" + k);

		Peticion.peticionGet(ip, Peticion.PROPUESTA,
				"proceso=" + numProceso + "&" + "k=" + k + "&" + "orden=" + ordenMensaje);
		return "OK";
	}

	@Path("propuesta")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String propuesta(@QueryParam(value = "k") String k, @QueryParam(value = "orden") String ordenj) {
		if (mensaje.getOrden().compareTo(ordenj) < 0) {
			mensaje.setOrden(ordenj);
		}

		lc2(ordenj);
		mensaje.setNumPropuestas(mensaje.getNumPropuestas() + 1);
		if (mensaje.getNumPropuestas() == ipServidores.length * 2) {
			mensaje.setEstado(Mensaje.DEFINITIVO);
			bMulticast(k, mensaje.getOrden(), Peticion.ACUERDO);
		}
		return "OK";
	}

	@Path("acuerdo")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String acuerdo(@QueryParam(value = "k") String k, @QueryParam(value = "orden") String ordenj) {

//		for (Mensaje m : cola) {
//			if (m.getId().equals(k)) {
//				mensaje = m;
//				break;
//			}
//		}
//
//		mensaje.setOrden(ordenj);
//		lc2(ordenj);
//		mensaje.setEstado(Mensaje.DEFINITIVO);
//
//		return "OK";
		
		System.out.println("proceso " + idProceso + ":   orden=" + ordenj + " k=" + k);
		return "OK";
	}

	private void lc1() {
		orden += 1;
	}

	private void lc2(String ordenj) {
		int valorOrdenj = Integer.parseInt(ordenj.split("\\.")[0]);

		if (orden > valorOrdenj) {
			orden += 1;
		} else {
			orden = valorOrdenj + 1;
		}
	}

	private void bMulticast(Mensaje m, String metodo) {

		for (String ip : ipServidores) {

			Peticion.peticionGet(ip, metodo, "proceso=1&" + "m=" + m.getContenido() + "&" + "k=" + m.getId());
			Peticion.peticionGet(ip, metodo, "proceso=2&" + "m=" + m.getContenido() + "&" + "k=" + m.getId());

			try {
				Thread.sleep((long) ((Math.random() * 0.3 + 0.2) * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void bMulticast(String k, String orden, String metodo) {

		for (String ip : ipServidores) {

			Peticion.peticionGet(ip, metodo, "proceso=1&" + "k=" + k + "&" + "orden=" + orden);
			Peticion.peticionGet(ip, metodo, "proceso=2&" + "k=" + k + "&" + "orden=" + orden);

			try {
				Thread.sleep((long) ((Math.random() * 0.3 + 0.2) * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
