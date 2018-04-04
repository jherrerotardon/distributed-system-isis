package data;

public class Mensaje {
	private final String id;
	private final String contenido;
	private final String orden;
	private final String numPeticiones;
	private final String estado;

	public Mensaje(long idProceso, int orden, int numPeticiones, String estado) {
		this.contenido = "P" + idProceso + " " + orden;
		this.id = orden + "." + idProceso;
		this.orden = "" + orden;
		this.numPeticiones = "" + numPeticiones;
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
	
	public String getNumPeticiones() {
		return numPeticiones;
	}
	
	public String getEstado() {
		return estado;
	}

	
	
}
