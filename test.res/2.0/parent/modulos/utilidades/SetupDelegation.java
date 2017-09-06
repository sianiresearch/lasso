package coordinacion.modulos.utilidades;

import coordinacion.incidencias.Incidencia;
import org.monet.bpi.DelegationSetup;
import org.monet.bpi.Task;
import org.monet.bpi.types.Date;
import org.monet.bpi.types.Term;

public class SetupDelegation {

	public static void configurarEncargo(
			coordinacion.modulos.asistentes.ConfigurarEncargoCorrectivo f,
			Task tarea, Incidencia incidencia) {

		Date fechaInicial = new Date();
		Date fechaFinal = new Date();
		Term campoPrioridad = incidencia.getPrioridad();
		prioridad(campoPrioridad, fechaInicial, fechaFinal);
		rellenarAsistente(f, fechaInicial, fechaFinal, incidencia.getDescripcion(), incidencia.getUrgente());
	}

	public static void rellenarAsistente(coordinacion.modulos.asistentes.ConfigurarEncargoCorrectivo f, Date fechaInicial, Date fechaFinal, String obs, Boolean urgente) {
		f.setFechaInicio(fechaInicial);
		f.setFechaFinLimite(fechaFinal);
		f.setObservaciones(obs);
		f.setUrgente(urgente);
	}

	public static void prioridad(Term campoPrioridad, Date fechaInicial,
								 Date fechaFinal) {
		if (campoPrioridad != null) {
			String prioridad = campoPrioridad.getKey();
			if (prioridad.equals("p001")) {
				fechaFinal = fechaFinal.plusDays(2);
			} else {
				if (prioridad.equals("p002")) {
					fechaFinal = fechaFinal.plusDays(7);
				} else {
					if (prioridad.equals("p003")) {
						fechaFinal = fechaFinal.plusDays(15);
					}
				}
			}
		}
	}

	public static void configurar(DelegationSetup ds, Task tarea,
								  Incidencia incidencia) {
		Date fechaInicial = new Date();
		Date fechaFinal = new Date();
		Term campoPrioridad = incidencia.getPrioridad();
		String obs = incidencia.getDescripcion();
		boolean urgente = incidencia.getUrgente();
		prioridad(campoPrioridad, fechaInicial, fechaFinal);
		ds.withDefaultValues(fechaInicial, fechaFinal, obs, urgente);
	}

	public static void configurarJob(org.monet.bpi.JobSetup js, Incidencia incidencia) {
		Date fechaInicial = new Date();
		Date fechaFinal = new Date();

		Term campoPrioridad = incidencia.getPrioridad();
		String obs = incidencia.getDescripcion();
		boolean urgente = incidencia.getUrgente();
		prioridad(campoPrioridad, fechaInicial, fechaFinal);
		js.withDefaultValues(fechaInicial, fechaFinal, obs, urgente);
	}

}
