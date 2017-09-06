package coordinacion.modulos.utilidades;

import coordinacion.analytics.dashboardincidencias.DataStoreIncidencia;
import coordinacion.analytics.dashboardincidencias.datastoreincidencia.*;
import coordinacion.procesos.copa.FichaExpediente;
import coordinacion.procesos.copa.ResultadoPa;
import coordinacion.procesos.copa.fichaexpediente.SeccionEncargo;
import coordinacion.procesos.copa.resultadopa.SeccionTrabajos;
import coordinacion.procesos.copa.resultadopa.secciontrabajos.SeccionOperaciones;
import coordinacion.procesos.copa.resultadopa.secciontrabajos.SeccionOperacionesRevision;
import org.monet.bpi.DatastoreService;
import org.monet.bpi.FieldComposite;
import org.monet.bpi.types.Date;
import org.monet.bpi.types.Link;
import org.monet.bpi.types.Term;

import java.util.ArrayList;

public class HechosOperacionesPreventivas {

	public static void hechoOperacionControl(ResultadoPa resultadoPa) {
		coordinacion.procesos.copa.Expediente expediente = (coordinacion.procesos.copa.Expediente) resultadoPa.getOwner();
		FichaExpediente ficha = expediente.getFichaExpediente();
		SeccionEncargo seccionEncargo = (SeccionEncargo) ficha.getSeccionEncargoField();
		coordinacion.modulos.proveedores.Ficha fichaProveedor = Utilidad.obtenerProveedor(seccionEncargo.getUnidad());
		Link proveedor = fichaProveedor.toLink();

		DataStoreIncidencia dataStoreInstance = (DataStoreIncidencia) DatastoreService
				.get("coordinacion.analytics.dashboardincidencias.DataStoreIncidencia");
		CuboOperacionesControlCube cuboOperacionesControl = dataStoreInstance
				.getCuboOperacionesControl();
		anadirComponentesTipoTrabajo(dataStoreInstance.getTipoTrabajo(), "Preventivo");

		for (FieldComposite trabajos : resultadoPa.getSeccionTrabajosField()
				.getAllFields()) {
			SeccionTrabajos tr = (SeccionTrabajos) trabajos;
			Link objeto = tr.getObjetoActuacion();

			for (FieldComposite operaciones : tr.getSeccionOperacionesField()
					.getAllFields()) {
				SeccionOperaciones op = (SeccionOperaciones) operaciones;
				Term operacion = op.getOperacion();
				Term operacionPadre = op.getOperacionPadre();
				String valor = op.getValor();
				anadirComponentes(dataStoreInstance.getOperacionesControl(), operacion, proveedor, "Preventivo", op.getUnidad(), operacion, operacionPadre);
				CuboOperacionesControlCubeFact fact = cuboOperacionesControl.insertFact(new Date());
				rellenarFact(fact, objeto, operacion, proveedor, valor);
			}

		}
		cuboOperacionesControl.save();
	}

//	public static Term obtenerOperacionControlPadre(Term operacion){
//		Source sourceControl = SourceService.get("coordinacion.ejecucion.TesauroOperacionesControl", null);
//		return sourceControl.getParentTerm(operacion.getKey());
//		
//	}
//	public static Term obtenerOperacionRevisionPadre(Term operacion){
//		Source sourceControl = SourceService.get("coordinacion.ejecucion.TesauroOperaciones", null);
//		return sourceControl.getParentTerm(operacion.getKey());
//		
//	}	
//	

	public static void anadirComponentes(OperacionesControlDimension dimensionOperacionesControl, Term operacion, Link cliente, String trabajo, String unidad, Term nombre, Term padreNombre) {
		//operacion padre
		OperacionesControlDimensionComponent componenteOperacionesControlPadre = dimensionOperacionesControl.insertComponent(padreNombre.getKey());
		rellenarComponenteOperacionesControlPadre(componenteOperacionesControlPadre, cliente, trabajo, unidad, padreNombre);
		componenteOperacionesControlPadre.save();
		//operacion padre
		OperacionesControlDimensionComponent componenteOperacionesControl = dimensionOperacionesControl.insertComponent(operacion.getKey());
		rellenarComponenteOperacionesControl(componenteOperacionesControl, cliente, trabajo, unidad, nombre, padreNombre);
		componenteOperacionesControl.save();

	}

	public static void anadirComponentesRevision(OperacionesRevisionDimension dimensionOperacionesRevision, Term operacion, Term operacionPadre) {
		OperacionesRevisionDimensionComponent componenteOperacionesRevisionPadre = dimensionOperacionesRevision.insertComponent(operacionPadre.getKey());
		rellenarComponenteOperacionesRevisionPadre(componenteOperacionesRevisionPadre, operacionPadre);
		componenteOperacionesRevisionPadre.save();
		OperacionesRevisionDimensionComponent componenteOperacionesRevision = dimensionOperacionesRevision.insertComponent(operacion.getKey());
		rellenarComponenteOperacionesRevision(componenteOperacionesRevision, operacion, operacionPadre);
		componenteOperacionesRevision.save();

	}

	public static void rellenarComponenteOperacionesControlPadre(OperacionesControlDimensionComponent componente, Link cliente, String trabajo, String unidad, Term nombre) {

		componente.addNombre(nombre, new ArrayList<Term>());
		componente.addUnidad(unidad);

	}

	public static void rellenarComponenteOperacionesRevisionPadre(OperacionesRevisionDimensionComponent componente, Term nombre) {
		componente.addNombre(nombre, new ArrayList<Term>());
	}


	public static void rellenarComponenteOperacionesControl(OperacionesControlDimensionComponent componente, Link cliente, String trabajo, String unidad, Term nombre, Term padreNombre) {
		ArrayList<Term> padres = crearJerarquiaOperaciones(padreNombre);
		Term nombreUnidad = nombre;
		if (unidad == null) {
			unidad = "sin asignar";
		}
		nombreUnidad.setLabel(nombre.getLabel() + " " + unidad);
		componente.addNombre(nombreUnidad, padres);
		componente.addUnidad(unidad);

	}

	public static void rellenarComponenteOperacionesRevision(OperacionesRevisionDimensionComponent componente, Term nombre, Term padreNombre) {
		ArrayList<Term> padres = crearJerarquiaOperaciones(padreNombre);
		componente.addNombre(nombre, padres);
	}

	public static void anadirComponentesTipoTrabajo(TipoTrabajoDimension dimensionTipoTrabajo, String trabajo) {
		//TipoTrabajoDimensionComponent componenteTipoTrabajo = dimensionOperacionesRevision.inser
		TipoTrabajoDimensionComponent componenteTipoTrabajo = null;
		if (trabajo.equals("Correctivo")) {
			componenteTipoTrabajo = dimensionTipoTrabajo.insertComponent("001");
		} else {
			componenteTipoTrabajo = dimensionTipoTrabajo.insertComponent("002");
		}
		componenteTipoTrabajo.addNombre(trabajo);
		componenteTipoTrabajo.save();
	}

	public static ArrayList<Term> crearJerarquiaOperaciones(Term padreNombre) {
		ArrayList<Term> jerarquia = new ArrayList<Term>();
		jerarquia.add(padreNombre);
		return jerarquia;
	}

	public static void rellenarFact(CuboOperacionesControlCubeFact fact, Link objeto, Term operacion, Link cliente, String valor) {
		fact.setOperacionesControl(operacion.getKey());
		fact.setObjetosActuacion(objeto.getId());
		fact.setProveedores(cliente.getId());
		fact.setTipoTrabajo("002");
		fact.setMetrica(new Double(1));
		try {
			Double medida = Double.valueOf(valor);
			fact.setMetricaPredictiva(medida);
		} catch (Exception e) {
			fact.setMetricaPredictiva(new Double(0));
		}
	}

	public static void rellenarFactRevision(CuboOperacionesRevisionCubeFact fact, Link objeto, Term operacion, Link cliente) {
		fact.setOperacionesRevision(operacion.getKey());
		fact.setObjetosActuacion(objeto.getId());
		fact.setTipoTrabajo("002");
		fact.setProveedores(cliente.getId());
		fact.setMetrica(new Double(1));
	}

	public static void hechoOperacionRevisionPreventivo(ResultadoPa resultadoPa) {
		coordinacion.procesos.copa.Expediente expediente = (coordinacion.procesos.copa.Expediente) resultadoPa.getOwner();
		FichaExpediente ficha = expediente.getFichaExpediente();
		SeccionEncargo seccionEncargo = (SeccionEncargo) ficha.getSeccionEncargoField();
		coordinacion.modulos.proveedores.Ficha fichaProveedor = Utilidad.obtenerProveedor(seccionEncargo.getUnidad());
		Link proveedor = fichaProveedor.toLink();

		DataStoreIncidencia dataStoreInstance = (DataStoreIncidencia) DatastoreService
				.get("coordinacion.analytics.dashboardincidencias.DataStoreIncidencia");
		CuboOperacionesRevisionCube cuboOperacionesRevision = dataStoreInstance
				.getCuboOperacionesRevision();
		anadirComponentesTipoTrabajo(dataStoreInstance.getTipoTrabajo(), "Preventivo");

		for (FieldComposite trabajos : resultadoPa.getSeccionTrabajosField()
				.getAllFields()) {
			SeccionTrabajos tr = (SeccionTrabajos) trabajos;
			Link objeto = tr.getObjetoActuacion();

			for (FieldComposite oprevres : tr.getSeccionOperacionesRevisionField().getAllFields()) {
				SeccionOperacionesRevision seccionoprev = (SeccionOperacionesRevision) oprevres;
				Term operacion = seccionoprev.getOperacionRev();
				Term operacionPadre = seccionoprev.getOperacionRevPadre();
				anadirComponentesRevision(dataStoreInstance.getOperacionesRevision(), operacion, operacionPadre);
				CuboOperacionesRevisionCubeFact fact = cuboOperacionesRevision.insertFact(new Date());
				rellenarFactRevision(fact, objeto, operacion, proveedor);
			}

			//TermList lista = tr.getOperacionesRevisionField().getCheckedAsTermList();
			//for (Term operacion : lista.getAll()){
			//Term operacionPadre = obtenerOperacionRevisionPadre(operacion);
			//}
		}
		cuboOperacionesRevision.save();
	}

}
