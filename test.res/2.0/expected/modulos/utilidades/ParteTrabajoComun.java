package coordinacion.modulos.utilidades;

import coordinacion.modulos.encargos.encargo.ficha.SeccionJerarquiaOperaciones;
import coordinacion.procesos.cori.ptc.schema.Anexo;
import coordinacion.procesos.cori.ptc.schema.JerarquiaOperaciones;
import org.monet.bpi.types.Date;

public class ParteTrabajoComun {

	public static void importarEncargo(
			coordinacion.modulos.encargos.encargo.Ficha ficha,
			coordinacion.procesos.cori.ptc.Schema item) {
		ficha.setFechaInicioReal(item.getFechaInicioReal());
		ficha.setFechaFinReal(item.getFechaFinReal());
		ficha.setObservacionesResultado(item.getObservacionesResultado());
		ficha.setResultado(item.getResultado());
		ficha.setImagenResultado(item.getImagenResultado());
		for (Anexo anexo : item.getAnexo()) {
			if (anexo.getImagen1() != null) {
				ficha.getImagenesAnexoField().addNew(anexo.getImagen1());
			}
			if (anexo.getImagen2() != null) {
				ficha.getImagenesAnexoField().addNew(anexo.getImagen2());
			}
		}
		if (ficha.getCalculoCoste() != null) {
			ficha.setCalculoCoste(item.getCalculoCoste());
		}
		if (ficha.getCalculoCosteFacturable() != null) {
			ficha.setCalculoCosteFacturable(item.getCalculoCosteFacturable());
		}
		ficha.setFechaRecepcionPT(new Date());
		Utilidad.asignarMultiple(item.getAdjuntosResultado(), ficha.getAdjuntosResultadoField());

		for (JerarquiaOperaciones jerarquia : item.getJerarquiaOperaciones()) {
			ficha.getOperaciones().add(jerarquia.getHijo());
			SeccionJerarquiaOperaciones jerarquiaOperaciones = (SeccionJerarquiaOperaciones) ficha.getSeccionJerarquiaOperacionesField().addNew();
			jerarquiaOperaciones.setPadre(jerarquia.getCategoria());
			jerarquiaOperaciones.setHijo(jerarquia.getHijo());
		}
		ficha.setNivelInfestacion(item.getNivelInfestacionResultado());
		ficha.save();
//MERGE
//	}
//	
//	
//	public static void calcularTiempoMedio(
//			coordinacion.modulos.encargos.encargo.Ficha ficha,
//			coordinacion.incidencias.Incidencia incidencia) {
//		
//		Date fechaEnvioOT = ficha.getFechaEnvioOT();
//		Date fechaFinReal= ficha.getFechaFinReal();
//		int diferencia = 0;
//		if (fechaEnvioOT.before(fechaFinReal))
//			diferencia = ficha.getFechaEnvioOT().secondsUntil(ficha.getFechaFinReal());
//		incidencia.setTiempoMedioActuacion(diferencia);
//		
//		
//		
	}
}
