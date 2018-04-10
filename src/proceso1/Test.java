package proceso1;

import java.util.concurrent.Semaphore;

import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Singleton
@Path("test")
public class Test extends Thread{

	private String aux;
	private int var = 0;
	private Semaphore sem;
	
	
	public Test() {
		aux = "mierda0";
		//System.err.println("const " + aux);
		var++;
		sem = new Semaphore(0);
		//this.start();
	}
	
	@Override
	public void run() {
		for (int i = 0; i < 100; i++) {
			
			aux = "mierda"+i;
			System.out.println(aux);
			try {
				this.sleep((long) 2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Path("hola")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String hola(@QueryParam("parado") int parado) {
		
		if(parado == 1) {
			try {
				sem.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "Retorno";
	}
}
