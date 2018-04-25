package data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
	private final int NMENSAJES = 100;

	private List<String> ipServidores;
	private List<Mensaje> cola;
	private int orden;
	private int idProceso;
	private Mensaje[] mensajes;
	private Semaphore semaforoCola;
	private Semaphore semaforoPreparados;
	private Semaphore semaforoPropuesta;
	private File ficheroLog;

	@Context
	HttpServletRequest request;

	public Proceso() {
		this.cola = new ArrayList<>();
		this.orden = 0;
		this.semaforoPreparados = new Semaphore(0);
		this.semaforoPropuesta = new Semaphore(1);
		this.ipServidores = new ArrayList<>();
		this.ipServidores.add("localhost");
		this.mensajes = new Mensaje[NMENSAJES];
		this.semaforoCola = new Semaphore(1);
	}

	@Override
	public void run() {

		for (int i = 1; i <= NMENSAJES; i++) {
			String idMensaje = (char) (OFFSETASCII + idProceso) + "" + i;
			mensajes[i - 1] = new Mensaje(idMensaje, idProceso, orden);
			bMulticast(mensajes[i - 1], Peticion.MENSAJE);

			try {
				Thread.sleep((long) ((Math.random() * 0.5 + 1.0) * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
	public String inicializar(@QueryParam(value = "idproceso") int idProceso, @QueryParam(value = "ips") String ips) {
		String[] ipParams;
		this.idProceso = idProceso;
		if (ips != null) {
			ipParams = ips.split("\\*");

			for (String ip : ipParams) {
				this.ipServidores.add(ip);
			}
		}

		for (String ip : ipServidores) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Peticion.peticionGet(ip, Peticion.PREPARADO, "proceso=1");
				}
			}).start();

			new Thread(new Runnable() {
				@Override
				public void run() {
					Peticion.peticionGet(ip, Peticion.PREPARADO, "proceso=2");
				}
			}).start();

		}

		try {
			ficheroLog = new File(System.getProperty("user.home") + File.separator + "proceso" + idProceso + ".log");
			if (ficheroLog.exists()) {
				ficheroLog.delete();
			}
			ficheroLog.createNewFile();

		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			semaforoPreparados.acquire(ipServidores.size() * 2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.start();

		return "Proceso " + this.idProceso + ": OK\n";
	}

	@Path("mensaje")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String mensaje(@QueryParam(value = "emisor") int emisor, @QueryParam(value = "m") String m,
			@QueryParam(value = "k") String k) {

		String ip;
		int destinatario;

		synchronized (this.getClass()) {
			lc1();
		}
		// Recuperacion y modificación del mensaje antes de guardarlo en la cola
		String ordenMensaje = orden + "." + idProceso;
		Mensaje mensajeCola = new Mensaje(m, k, ordenMensaje, 0, Mensaje.PROVISIONAL);
		/***********************************/
		try {
			semaforoCola.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cola.add(mensajeCola);
		semaforoCola.release();
		/*********************************/

		// Reconversion de ipv6 a ipv4 cuando viene de localhost
		ip = request.getRemoteAddr().equals(LOCALHOSTIPV6) ? "localhost" : request.getRemoteAddr();

		destinatario = (emisor % 2 == 0) ? 2 : 1; // Procesos de 1 a 6, para
													// envio deben ser 1 o 2

		new Thread(new Runnable() {

			@Override
			public void run() {
				Peticion.peticionGet(ip, Peticion.PROPUESTA,
						"proceso=" + destinatario + "&" + "k=" + k + "&" + "orden=" + ordenMensaje);
			}
		}).start();

		return "OK";
	}

	@Path("propuesta")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String propuesta(@QueryParam(value = "k") String k, @QueryParam(value = "orden") String ordenj) {
		int indiceMensaje = Integer.parseInt(k.substring(1)) - 1;

		try {
			semaforoPropuesta.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (Mensaje.ComparatorMensaje.compareOrden(mensajes[indiceMensaje].getOrden(), ordenj) < 0) {
			mensajes[indiceMensaje].setOrden(ordenj);
		}

		synchronized (this.getClass()) {
			lc2(ordenj);
		}

		mensajes[indiceMensaje].setNumPropuestas(mensajes[indiceMensaje].getNumPropuestas() + 1);
		if (mensajes[indiceMensaje].getNumPropuestas() == ipServidores.size() * 2) {
			mensajes[indiceMensaje].setEstado(Mensaje.DEFINITIVO);
			String ordenPropuesta = mensajes[indiceMensaje].getOrden();

			semaforoPropuesta.release();

			bMulticast(k, ordenPropuesta, Peticion.ACUERDO);
		} else {
			semaforoPropuesta.release();
		}
		return "OK";
	}

	@Path("acuerdo")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String acuerdo(@QueryParam(value = "k") String k, @QueryParam(value = "orden") String ordenj) {

		// cola.get(k)
		Mensaje mensajeAcuerdo = null;
		for (Mensaje m : cola) {
			if (m.getId().equals(k)) {
				mensajeAcuerdo = m;
				break;
			}
		}

		mensajeAcuerdo.setOrden(ordenj);

		synchronized (this.getClass()) {
			lc2(ordenj);

			mensajeAcuerdo.setEstado(Mensaje.DEFINITIVO);

			cola.sort(new Mensaje.ComparatorMensaje());
		}

		// Escribir la cola en el fichero.
		try {
			semaforoCola.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		if (!cola.isEmpty()) {
			mensajeAcuerdo = cola.get(0);

			while (mensajeAcuerdo.getEstado().compareTo(Mensaje.DEFINITIVO) == 0) {
				// Escritura mensaje en fichero
				try {

					Files.write(Paths.get(ficheroLog.getPath()),
							(mensajeAcuerdo.getId() + " " + mensajeAcuerdo.getOrden() + "\n").getBytes(),
							StandardOpenOption.APPEND);

				} catch (IOException e) {
					e.printStackTrace();
				}

				cola.remove(0);
				if (cola.isEmpty()) {
					break;
				}
				mensajeAcuerdo = cola.get(0);
			}
		}
		semaforoCola.release();

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
			new Thread(new Runnable() {

				@Override
				public void run() {
					Peticion.peticionGet(ip, metodo, "proceso=1" + "&emisor=" + idProceso + "&m="
							+ m.getContenido().replace(' ', '+') + "&" + "k=" + m.getId());
				}
			}).start();

			try {
				Thread.sleep((long) ((Math.random() * 0.3 + 0.2) * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			new Thread(new Runnable() {

				@Override
				public void run() {
					Peticion.peticionGet(ip, metodo, "proceso=2" + "&emisor=" + idProceso + "&m="
							+ m.getContenido().replace(' ', '+') + "&" + "k=" + m.getId());
				}
			}).start();

			try {
				Thread.sleep((long) ((Math.random() * 0.3 + 0.2) * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void bMulticast(String k, String orden, String metodo) {

		for (String ip : ipServidores) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					Peticion.peticionGet(ip, metodo, "proceso=1&" + "k=" + k + "&" + "orden=" + orden);
				}
			}).start();

			try {
				Thread.sleep((long) ((Math.random() * 0.3 + 0.2) * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			new Thread(new Runnable() {

				@Override
				public void run() {
					Peticion.peticionGet(ip, metodo, "proceso=2&" + "k=" + k + "&" + "orden=" + orden);
				}
			}).start();

			try {
				Thread.sleep((long) ((Math.random() * 0.3 + 0.2) * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
