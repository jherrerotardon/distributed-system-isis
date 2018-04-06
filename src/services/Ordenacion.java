package services;

import java.io.IOException;

import javax.inject.Singleton;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

@Singleton
@Path("ordenacion")
public class Ordenacion {

	@Context
	HttpServletRequest request;
	@Context
	HttpServletResponse response;
	@Context
	ServletContext context;

	@Path("mensaje")
	@GET
	public void mensaje(@QueryParam(value = "proceso") int numProceso){
		
		RequestDispatcher dispatcher =  context.getRequestDispatcher("/proceso" + numProceso + "/proceso/mensaje");
		try {
			dispatcher.forward(request, response);
		} catch (ServletException e) {
			System.out.println("ServletException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
		
	}

	@Path("propuesta")
	@GET
	public void propuesta(@QueryParam(value = "proceso") int numProceso) {

		RequestDispatcher dispatcher =  context.getRequestDispatcher("/proceso" + numProceso + "/proceso/propuesta");
		try {
			dispatcher.forward(request, response);
		} catch (ServletException e) {
			System.out.println("ServletException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
	}

	@Path("acuerdo")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public void acuerdo(@QueryParam(value = "proceso") int numProceso) {

		RequestDispatcher dispatcher =  context.getRequestDispatcher("/proceso" + numProceso + "/proceso/acuerdo");
		try {
			dispatcher.forward(request, response);
		} catch (ServletException e) {
			System.out.println("ServletException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
	}
	
	@Path("inicializar")
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public void inicializar(@QueryParam(value = "proceso") int numProceso) {

		RequestDispatcher dispatcher =  context.getRequestDispatcher("/proceso" + numProceso + "/proceso/inicializar");
		try {
			dispatcher.forward(request, response);
		} catch (ServletException e) {
			System.out.println("ServletException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
	}
}
