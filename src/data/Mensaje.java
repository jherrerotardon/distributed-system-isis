package data;

import java.util.Comparator;

public class Mensaje {

	public static final String PROVISIONAL = "PROVISIONAL";
	public static final String DEFINITIVO = "DEFINITIVO";
	
	private  String contenido;
	private  String id;
	private  String orden;
	private  int numPropuestas;
	private  String estado;

	public Mensaje(String id,  int idProceso, int numMensaje) {
		this.contenido = "P" + idProceso + " " + numMensaje;
		this.id = id;
		this.orden = numMensaje + "." + idProceso;
	}

	public Mensaje(String contenido, String id, String orden, int numPeticiones, String estado) {
		this.contenido = contenido;
		this.id = id;
		this.orden = orden;
		this.numPropuestas = numPeticiones;
		this.estado = estado;
	}
	
	public String getContenido() {
		return contenido;
	}
	
	public String getId() {
		return id;
	}

	public String getOrden() {
		return orden;
	}
	
	public int getNumPropuestas() {
		return numPropuestas;
	}
	
	public String getEstado() {
		return estado;
	}

	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public void setOrden(String orden) {
		this.orden = orden;
	}
	public void setNumPropuestas(int numPropuestas) {
		this.numPropuestas = numPropuestas;
	}
	
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
	public static class ComparatorMensaje implements Comparator<Mensaje>{

		@Override
		public int compare(Mensaje o1, Mensaje o2) {
			return o1.getOrden().compareTo(o2.getOrden());
			
		}
	}
}
