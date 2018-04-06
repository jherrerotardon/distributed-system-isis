package proceso1;

import javax.annotation.PostConstruct;

import data.DatosProceso;
import data.Proceso;

public class Proceso1 extends Proceso{

	public Proceso1(DatosProceso datos) {
		super(datos);
		System.out.println("Proceso 1");
	}
	
	@PostConstruct
	private void constructor() {
		super.datos = new DatosProceso();
		super.hilo = new Proceso1(datos);
	}

}
