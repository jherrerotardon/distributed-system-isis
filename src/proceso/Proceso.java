package proceso;

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

import data.Mensaje;
import utils.Peticion;

@Singleton
@Path("proceso")
public class Proceso extends Thread {

	private final String LOCALHOSTIPV6 = "0:0:0:0:0:0:0:1";
	private final char OFFSETASCII = 64; // Caracter generador de identificadores unico de mensaje.
	private final int NMENSAJES = 100;

	private List<String> ipServidores;
	private List<Mensaje> cola;
	private int orden;
	private int idProceso;
	private Mensaje[] mensajes;
	private Semaphore semaforoCola; // Semaforo utilizado para sincronizar el acceso a la cola.
	private Semaphore semaforoPreparados; // Semaforo utilizado para esperar que todos los procesos se inicialicen.
	private Semaphore semaforoPropuesta; // Semaforo utilizado para sincronizar la llegada de propuestas.
	private File ficheroLog;

	@Context
	HttpServletRequest request; // Almacenamiento de datos de la peticion, usado para obtener la IP del emisor
								// de la peticion.

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

	/*
	 * Metodo run del hilo. Genera una cantidad de mensajes que son multidifundufos
	 * a todos los procesos.
	 */
	@Override
	public void run() {

		for (int i = 1; i <= NMENSAJES; i++) {
			String idMensaje = (char) (OFFSETASCII + idProceso) + "" + i;
			mensajes[i - 1] = new Mensaje(idMensaje, idProceso, i);
			bMulticast(mensajes[i - 1], Peticion.MENSAJE);

			try {
				Thread.sleep((long) ((Math.random() * 0.5 + 1.0) * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/*
	 * Servicio preparado. Encargado de recibir una peticion cada vez que un proceso
	 * se ha inicializado correctamente. Incrementa un semaforo para dar comienzo al
	 * algoritmo.
	 */
	@Path("preparado")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String preparado() {
		semaforoPreparados.release();
		return "OK";
	}

	/*
	 * Servicio inicializar. Se reciben por parametro el identificador de proceso y
	 * las IPs de cada servidor. Con estos valores se inicializan todos los
	 * variables de instancia. Una vez inicializadas correctamente se indica a los
	 * demas procesos que dicho proceso esta listo.
	 */
	@Path("inicializar")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String inicializar(@QueryParam(value = "idproceso") int idProceso, @QueryParam(value = "ips") String ips) {
		String[] ipParams;

		this.idProceso = idProceso;

		// Almacenar las IPs en un vector, las cuales son separadas por el caracter '*'.
		if (ips != null && !ips.equals("")) {
			ipParams = ips.split("\\*");

			for (String ip : ipParams) {
				this.ipServidores.add(ip);
			}
		}

		// Peticiones a los demas procesos de que se está preparado.
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

		// Creacion del fichero de log en la carpeta personal del usuario.
		try {
			ficheroLog = new File(System.getProperty("user.home") + File.separator + "proceso" + idProceso + ".log");
			if (ficheroLog.exists()) {
				ficheroLog.delete();
			}
			ficheroLog.createNewFile();

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Esperar hasta que todos los procesos estan preparados.
		try {
			semaforoPreparados.acquire(ipServidores.size() * 2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		this.start();

		return "Proceso " + this.idProceso + ": OK\n";
	}

	/*
	 * Servicio mensaje. Se reciben los mensajes de los procesos y se añaden a la
	 * cola de mensajes. Por ultimo, se constesta con una propuesta al proceso
	 * emisor.
	 */
	@Path("mensaje")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String mensaje(@QueryParam(value = "emisor") int emisor, @QueryParam(value = "m") String m,
			@QueryParam(value = "k") String k) {

		String ip; // IP del emisor.
		int destinatario;

		// Sincronizado el incremento del orden por ser variable compartida entre los
		// hilos.
		synchronized (this.getClass()) {
			lc1();
		}

		// Recuperacion y modificacion del mensaje antes de guardarlo en la cola
		String ordenMensaje = orden + "." + idProceso;
		Mensaje mensajeCola = new Mensaje(m, k, ordenMensaje, 0, Mensaje.PROVISIONAL);

		// Sincronizado el acceso a la cola debido a la posibilidad de que varios hilos
		// añadan a la cola a la vez.
		try {
			semaforoCola.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cola.add(mensajeCola);
		semaforoCola.release();

		// Reconversion de ipv6 a ipv4 cuando viene de localhost
		ip = request.getRemoteAddr().equals(LOCALHOSTIPV6) ? "localhost" : request.getRemoteAddr();

		destinatario = (emisor % 2 == 0) ? 2 : 1; // Transformacion de identificador de proceso de 1 a 6 a 1 o 2 para el
													// envio.
		// Envio de la propuesta.
		new Thread(new Runnable() {
			@Override
			public void run() {
				Peticion.peticionGet(ip, Peticion.PROPUESTA,
						"proceso=" + destinatario + "&" + "k=" + k + "&" + "orden=" + ordenMensaje);
			}
		}).start();

		return "OK";
	}

	/*
	 * Servicio propuesta. Se reciben las propuestas de los demás procesos. Una vez
	 * recibidas propuestas de todos los procesos se multidifunde un acuerdo para
	 * ese mensaje determinado eligiendo el orden mayor de entre todas las
	 * propuestas.
	 */
	@Path("propuesta")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String propuesta(@QueryParam(value = "k") String k, @QueryParam(value = "orden") String ordenj) {
		int indiceMensaje = Integer.parseInt(k.substring(1)) - 1;

		// Sincronizado el acceso al mensaje recibido y a la variable orden del proceso.
		try {
			semaforoPropuesta.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// Modificacion del orden del mensaje si es menor que el orden propuesto.
		if (Mensaje.ComparatorMensaje.compareOrden(mensajes[indiceMensaje].getOrden(), ordenj) < 0) {
			mensajes[indiceMensaje].setOrden(ordenj);
		}

		// Sincronizado el incremento del orden del proceso.
		synchronized (this.getClass()) {
			lc2(ordenj);
		}

		// Multidifundir acuerdo si se han recibido propuestas de dicho mensaje de todos
		// los procesos.
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

	/*
	 * Servicio acuerdo. Recibe al cuerdo de un determnado mensaje y lo cambia a
	 * DEFINITIVO. A su vez, imprime en el fichero log los mensajes que se
	 * encuentran en la cola en estado DEFINITIVO siempre y cuando no haya otro
	 * mensaje con un orden menor con estado PROVISIONAL.
	 */
	@Path("acuerdo")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String acuerdo(@QueryParam(value = "k") String k, @QueryParam(value = "orden") String ordenj) {

		// Recuperar de la cola el mensaje con identificador k.
		try {
			semaforoCola.acquire();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		Mensaje mensajeAcuerdo = null;
		for (Mensaje m : cola) {
			if (m.getId().equals(k)) {
				mensajeAcuerdo = m;
				break;
			}
		}
		semaforoCola.release();

		// Establecer orden y estado final del mensaje.
		mensajeAcuerdo.setOrden(ordenj);
		mensajeAcuerdo.setEstado(Mensaje.DEFINITIVO);

		// Sincronizado el incremento del orden del proceso.
		synchronized (this.getClass()) {
			lc2(ordenj);
		}

		// Sincronizado el acceso y manipulacion de la cola de mensajes.
		try {
			semaforoCola.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!cola.isEmpty()) {

			// Reordenacion de la cola de mensajes (de menor a mayor) usando su orden.
			cola.sort(new Mensaje.ComparatorMensaje());

			mensajeAcuerdo = cola.get(0);

			// Escritura de la cola de mensajes en el fichero log. Se escriben mensajes de
			// la cola mientras sean DEFNITIVOS.
			while (mensajeAcuerdo.getEstado().compareTo(Mensaje.DEFINITIVO) == 0) {
				try {

					Files.write(Paths.get(ficheroLog.getPath()), (mensajeAcuerdo.getContenido() + '\n').getBytes(),
							StandardOpenOption.APPEND);

				} catch (IOException e) {
					e.printStackTrace();
				}

				// Eliminacion del mesaje que ya se ha ecrito en el fichero.
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

	// Incremento del orden de proceso.
	private void lc1() {
		orden += 1;
	}

	// Actualizacion del orden de proceso en funcion del orden recibido por
	// parametro.
	private void lc2(String ordenj) {
		int valorOrdenj = Integer.parseInt(ordenj.split("\\.")[0]);

		if (orden > valorOrdenj) {
			orden += 1;
		} else {
			orden = valorOrdenj + 1;
		}
	}

	// Multidifusion del mensaje recibido por parametro a todas las IPs.
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

	// Multidifusion del mensaje recibido por parametro a todas las IPs.
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
