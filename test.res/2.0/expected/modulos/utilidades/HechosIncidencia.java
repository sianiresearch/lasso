package coordinacion.modulos.utilidades;

import coordinacion.analytics.dashboardincidencias.DataStoreIncidencia;
import coordinacion.analytics.dashboardincidencias.datastoreincidencia.*;
import coordinacion.incidencias.Incidencia;
import coordinacion.modulos.proveedores.Ficha;
import org.monet.bpi.ConsoleService;
import org.monet.bpi.DatastoreService;
import org.monet.bpi.Expression;
import org.monet.bpi.Param;
import org.monet.bpi.types.Date;
import org.monet.bpi.types.Term;

import java.util.ArrayList;

public class HechosIncidencia {

	public static void insertarTipo(DataStoreIncidencia dataStoreInstance, Term tipo) {
		TiposIncidenciaDimensionComponent tipoIncidencia = dataStoreInstance.getTiposIncidencia().insertComponent(tipo.getKey());
		tipoIncidencia.addTipoIncidencia(tipo.getLabel());
		tipoIncidencia.save();
	}

	public static void hechoIncidenciaTramitada(Incidencia incidencia) {
		try {
			hechoActualizarAbiertas();
		} catch (Exception e) {
			ConsoleService.println("ERROR: La actualización del cubo de incidencias abiertas de ejecución ha fallado");
			e.printStackTrace();
		}
		DataStoreIncidencia dataStoreInstance = (DataStoreIncidencia) DatastoreService
				.get("coordinacion.analytics.dashboardincidencias.DataStoreIncidencia");
		CuboIncidenciasTramitadasCube cuboIncidencia = dataStoreInstance
				.getCuboIncidenciasTramitadas();
		for (Term tipo : incidencia.getTipoIncidenciaField().getAll()) {
			CuboIncidenciasTramitadasCubeFact fact = cuboIncidencia
					.insertFact(new Date());
			fact.setIncidencias(incidencia.toLink().getId());
			fact.setObjetosActuacion(incidencia.getObjetoActuacion().getId());
			Ficha proveedor = Utilidad.obtenerProveedor(incidencia.getUnidad());
			fact.setProveedores(proveedor.toLink().getId());
			insertarTipo(dataStoreInstance, tipo);
			fact.setTiposIncidencia(tipo.getKey());
			fact.setMetrica(new Double(1));
		}
		cuboIncidencia.save();
	}

	public static void hechoIncidenciaVerificada(Incidencia incidencia) {
		Date fecha = incidencia.getFechaVerificacion();
		if (fecha == null) {
			incidencia.setFechaVerificacion(new Date());
			incidencia.save();
			fecha = incidencia.getFechaVerificacion();
		}
		DataStoreIncidencia dataStoreInstance = (DataStoreIncidencia) DatastoreService
				.get("coordinacion.analytics.dashboardincidencias.DataStoreIncidencia");
		CuboIncidenciasVerificadasCube cuboIncidencia = dataStoreInstance
				.getCuboIncidenciasVerificadas();
		for (Term tipo : incidencia.getTipoIncidenciaField().getAll()) {
			CuboIncidenciasVerificadasCubeFact fact = cuboIncidencia
					.insertFact(fecha);
			fact.setIncidencias(incidencia.toLink().getId());
			fact.setObjetosActuacion(incidencia.getObjetoActuacion().getId());
			Ficha proveedor = Utilidad.obtenerProveedor(incidencia.getUnidad());
			fact.setProveedores(proveedor.toLink().getId());
			insertarTipo(dataStoreInstance, tipo);
			fact.setTiposIncidencia(tipo.getKey());
			fact.setMetrica(new Double(1));
		}
		cuboIncidencia.save();
	}

	public static void hechoIncidenciaCorregida(Incidencia incidencia) {
		Date fecha = incidencia.getFechaEjecucion();
		if (fecha == null) {
			incidencia.setFechaEjecucion(new Date());
			incidencia.save();
			fecha = incidencia.getFechaEjecucion();
		}
		DataStoreIncidencia dataStoreInstance = (DataStoreIncidencia) DatastoreService
				.get("coordinacion.analytics.dashboardincidencias.DataStoreIncidencia");
		CuboIncidenciasCorregidasCube cuboIncidencia = dataStoreInstance
				.getCuboIncidenciasCorregidas();
		for (Term tipo : incidencia.getTipoIncidenciaField().getAll()) {
			CuboIncidenciasCorregidasCubeFact fact = cuboIncidencia
					.insertFact(fecha);
			fact.setIncidencias(incidencia.toLink().getId());
			fact.setObjetosActuacion(incidencia.getObjetoActuacion().getId());
			Ficha proveedor = Utilidad.obtenerProveedor(incidencia.getUnidad());
			fact.setProveedores(proveedor.toLink().getId());
			insertarTipo(dataStoreInstance, tipo);
			fact.setTiposIncidencia(tipo.getKey());
			fact.setMetrica(new Double(1));
		}
		cuboIncidencia.save();
	}

	public static void hechoIncidenciaInspeccionada(Incidencia incidencia) {
		Date fecha = incidencia.getFechaInspeccion();
		if (fecha == null) {
			incidencia.setFechaInspeccion(new Date());
			incidencia.save();
			fecha = incidencia.getFechaInspeccion();
		}
		DataStoreIncidencia dataStoreInstance = (DataStoreIncidencia) DatastoreService
				.get("coordinacion.analytics.dashboardincidencias.DataStoreIncidencia");
		CuboIncidenciasInspeccionadasCube cuboIncidencia = dataStoreInstance
				.getCuboIncidenciasInspeccionadas();
		for (Term tipo : incidencia.getTipoIncidenciaField().getAll()) {
			CuboIncidenciasInspeccionadasCubeFact fact = cuboIncidencia
					.insertFact(fecha);
			fact.setIncidencias(incidencia.toLink().getId());
			fact.setObjetosActuacion(incidencia.getObjetoActuacion().getId());
			Ficha proveedor = Utilidad.obtenerProveedor(incidencia.getUnidad());
			fact.setProveedores(proveedor.toLink().getId());
			insertarTipo(dataStoreInstance, tipo);
			fact.setTiposIncidencia(tipo.getKey());
			fact.setMetrica(new Double(1));
		}
		cuboIncidencia.save();
	}

	public static void hechoIncidenciaCerrada(Incidencia incidencia) {
		try {
			hechoActualizarAbiertas();
		} catch (Exception e) {
			ConsoleService.println("ERROR: La actualización del cubo de incidencias abiertas de ejecución ha fallado");
			e.printStackTrace();
		}
		DataStoreIncidencia dataStoreInstance = (DataStoreIncidencia) DatastoreService
				.get("coordinacion.analytics.dashboardincidencias.DataStoreIncidencia");
		CuboIncidenciasCerradasCube cuboIncidencia = dataStoreInstance
				.getCuboIncidenciasCerradas();
		for (Term tipo : incidencia.getTipoIncidenciaField().getAll()) {
			CuboIncidenciasCerradasCubeFact fact = cuboIncidencia
					.insertFact(new Date());
			fact.setIncidencias(incidencia.toLink().getId());
			fact.setObjetosActuacion(incidencia.getObjetoActuacion().getId());
			Ficha proveedor = Utilidad.obtenerProveedor(incidencia.getUnidad());
			fact.setProveedores(proveedor.toLink().getId());
			insertarTipo(dataStoreInstance, tipo);
			fact.setTiposIncidencia(tipo.getKey());
			fact.setMetrica(new Double(1));
		}
		cuboIncidencia.save();
	}

	public static void hechoIncidenciaCancelada(Incidencia incidencia) {
		try {
			hechoActualizarAbiertas();
		} catch (Exception e) {
			ConsoleService.println("ERROR: La actualización del cubo de incidencias abiertas de ejecución ha fallado");
			e.printStackTrace();
		}
		DataStoreIncidencia dataStoreInstance = (DataStoreIncidencia) DatastoreService
				.get("coordinacion.analytics.dashboardincidencias.DataStoreIncidencia");
		CuboIncidenciasCanceladasCube cuboIncidencia = dataStoreInstance
				.getCuboIncidenciasCanceladas();
		for (Term tipo : incidencia.getTipoIncidenciaField().getAll()) {
			CuboIncidenciasCanceladasCubeFact fact = cuboIncidencia
					.insertFact(new Date());
			fact.setIncidencias(incidencia.toLink().getId());
			fact.setObjetosActuacion(incidencia.getObjetoActuacion().getId());
			insertarTipo(dataStoreInstance, tipo);
			fact.setTiposIncidencia(tipo.getKey());
			fact.setMetrica(new Double(1));
		}
		cuboIncidencia.save();
	}

	public static Date actualizarAbiertas() {
		try {
			coordinacion.Preferencias preferencias = coordinacion.Preferencias.getInstance();
			Date fechaAnterior = preferencias.getFechaCuboIncidenciasAbiertas();
			if (fechaAnterior == null) {
				preferencias.setFechaCuboIncidenciasAbiertas(new Date());
				preferencias.save();
				return null;
			}
			//Date hoy = new Date();
			preferencias.setFechaFinalCuboAbiertas(new Date());
			Date hoy = preferencias.getFechaFinalCuboAbiertas();
			if (hoy == null) hoy = new Date();
			if (fechaAnterior.before(hoy)) {
				Date fechaNueva = new Date(fechaAnterior.getValue());
				preferencias.setFechaCuboIncidenciasAbiertas(fechaNueva.plusDays(1));
				preferencias.save();
				return fechaAnterior;
			}
			if (fechaAnterior.equals(hoy)) {
				return null;
			}
			return null;
		} catch (Exception e) {
			ConsoleService.println("ERROR: La actualización del cubo de incidencias abiertas de ejecución ha fallado");
			e.printStackTrace();
			return null;
		}
	}

	public static void hechoActualizarAbiertas() {
		Date fechaHecho = actualizarAbiertas();
		ArrayList<Incidencia> listaIncidencias = null;
		DataStoreIncidencia dataStoreInstance = (DataStoreIncidencia) DatastoreService.get("coordinacion.analytics.dashboardincidencias.DataStoreIncidencia");
		CuboEvolucionTrabajosPendienteCube cuboIncidencia = dataStoreInstance.getCuboEvolucionTrabajosPendiente();
		while (fechaHecho != null) {
			coordinacion.incidencias.Coleccion incidencias = coordinacion.incidencias.Coleccion.getInstance();
			if (listaIncidencias == null)
				listaIncidencias = obtenerIncidenciasAbiertas(incidencias);
			for (Incidencia elemento : listaIncidencias) {
				hechoIncidenciaAbierta(elemento, cuboIncidencia, fechaHecho, listaIncidencias.size());
			}
			fechaHecho = actualizarAbiertas();
		}
		cuboIncidencia.save();
	}

	public static ArrayList<Incidencia> obtenerIncidenciasAbiertas(coordinacion.incidencias.Coleccion incidencias) {
		ArrayList<Incidencia> listaIncidencias = new ArrayList<Incidencia>();
		Param parameter = coordinacion.incidencias.coleccion.Index.Estado;
		Expression exp = Expression.Or(parameter.Eq("Verificada"), parameter.Eq("Verificando"), parameter.Eq("Ejecutada"), parameter.Eq("Corrigiendo"), parameter.Eq("Inspeccionada"), parameter.Eq("Inspeccionando"));
		for (coordinacion.incidencias.coleccion.Index elemento : incidencias.get(exp)) {
			Incidencia incidencia = (coordinacion.incidencias.Incidencia) elemento.getIndexedNode();
			listaIncidencias.add(incidencia);
		}
		return listaIncidencias;
	}

	public static void hechoIncidenciaAbierta(coordinacion.incidencias.Incidencia incidencia, CuboEvolucionTrabajosPendienteCube cuboIncidencia, Date fechaHecho, int totalIncidencias) {
		CuboEvolucionTrabajosPendienteCubeFact fact = cuboIncidencia.insertFact(fechaHecho);
		fact.setIncidencias(incidencia.toLink().getId());
		fact.setObjetosActuacion(incidencia.getObjetoActuacion().getId());
		Ficha proveedor = Utilidad.obtenerProveedor(incidencia.getUnidad());
		fact.setProveedores(proveedor.toLink().getId());
		fact.setMetrica(new Double(1));
	}


}
