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
		public int compare(Mensaje m1, Mensaje m2) {
			String[] camposM1 = m1.getOrden().split("\\.");
			String[] camposM2 = m2.getOrden().split("\\.");
			
			int ordenM1 = Integer.parseInt(camposM1[0]);
			int ordenM2 = Integer.parseInt(camposM2[0]);
			
			if (ordenM1 < ordenM2) {
				return -1;
			}
			else if (ordenM1 > ordenM2) {
				return 1;
			}
			else {
				int idProcesoM1 = Integer.parseInt(camposM1[1]);
				int idProcesoM2 = Integer.parseInt(camposM2[1]);
				
				if (idProcesoM1 < idProcesoM2) {
					return -1;
				}
				else if (idProcesoM1 > idProcesoM2) {
					return 1;
				}
				else {
					return 0;
				}
			}
			
		}
	}
}
