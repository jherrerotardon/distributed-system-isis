package proceso2;

import javax.annotation.PostConstruct;

import data.DatosProceso;
import data.Proceso;

public class Proceso2 extends Proceso{

	public Proceso2(DatosProceso datos) {
		super(datos);
		System.out.println("Proceso 2");
	}
	
	@PostConstruct
	private void constructor() {
		super.datos = new DatosProceso();
		super.hilo = new Proceso2(datos);
	}

}
