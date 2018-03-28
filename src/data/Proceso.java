package data;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.ws.rs.core.UriBuilder;

import utils.Peticion;

public class Proceso extends Thread {
	private String[] ipProcesos;
	private Queue<Mensaje> cola;
	private int orden;

	public Proceso() {
		cola = new LinkedList<Mensaje>();
		orden = 0;
	}

	@Override
	public void run() {

		for (int i = 100; i < 100; i++) {
			Mensaje m = new Mensaje(this.getId(), orden);
			
			bMulticast(ipProcesos, m, Peticion.MENSAJE);

			try {
				Thread.sleep((long) ((Math.random() * 0.5 + 1.0) * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void bMulticast(String[] ipProcesos, Mensaje m, String metodo) {
		
		for (String ip : ipProcesos) {
			
			Peticion.peticionGet(ip, metodo, "m="+ m.getMensaje() + "&" + "k=" + m.getId());
			
			try {
				Thread.sleep((long) ((Math.random() * 0.3 + 0.2) * 1000));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		
	}
}
