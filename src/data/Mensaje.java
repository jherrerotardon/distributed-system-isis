package data;

public class Mensaje {
	private final String id;
	private final String mensaje;
	
	public Mensaje(long idProceso, int orden) {
		this.id = orden + "." + idProceso;
		this.mensaje = "P" + idProceso + " " + orden;
	}
	public String getId() {
		return id;
	}
	public String getMensaje() {
		return mensaje;
	}
	
}
