package data;

import java.util.LinkedList;

public class DatosProceso {
	private String[] ipServidores;
	private LinkedList<Mensaje> cola;
	private int orden;
	
	public DatosProceso(String[] ipServidores) {
		this.ipServidores = ipServidores;
		this.cola = new LinkedList<>();
		this.orden = 0;
	}
	
	public String[] getIpServidores() {
		return ipServidores;
	}
	
	public void setIpServidores(String[] ipServidores) {
		this.ipServidores = ipServidores;
	}
	
	public LinkedList<Mensaje> getCola() {
		return cola;
	}
	
	public void setCola(LinkedList<Mensaje> cola) {
		this.cola = cola;
	}
	
	public int getOrden() {
		return orden;
	}
	
	public void setOrden(int orden) {
		this.orden = orden;
	}
}
