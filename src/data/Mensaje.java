package data;

public class Mensaje {

	public static final String PROVISIONAL = "PROVISIONAL";
	public static final String DEFINITIVO = "DEFINITIVO";
	
	private  String contenido;
	private  String id;
	private  int orden;
	private  int numPeticiones;
	private  String estado;

	public Mensaje(long idProceso, int orden) {
		this.contenido = "P" + idProceso + " " + orden;
		this.id = orden + "." + idProceso;
		this.orden = orden;
	}

	public Mensaje(String contenido, String id, int orden, int numPeticiones, String estado) {
		this.contenido = contenido;
		this.id = id;
		this.orden = orden;
		this.numPeticiones = numPeticiones;
		this.estado = estado;
	}
	
	public String getContenido() {
		return contenido;
	}
	
	public String getId() {
		return id;
	}

	public int getOrden() {
		return orden;
	}
	
	public int getNumPeticiones() {
		return numPeticiones;
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
	
	public void setOrden(int orden) {
		this.orden = orden;
	}
	public void setNumPeticiones(int numPeticiones) {
		this.numPeticiones = numPeticiones;
	}
	
	public void setEstado(String estado) {
		this.estado = estado;
	}
	
}
