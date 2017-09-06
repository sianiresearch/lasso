package coordinacion.modulos.utilidades;

import coordinacion.analytics.dashboardincidencias.DataStoreIncidencia;
import coordinacion.analytics.dashboardincidencias.datastoreincidencia.*;
import coordinacion.modulos.encargos.Coleccion;
import coordinacion.modulos.encargos.encargo.ficha.SeccionJerarquiaOperaciones;
import coordinacion.procesos.cori.Expediente;
import coordinacion.procesos.cori.encargos.EncargoCA;
import coordinacion.procesos.cori.encargos.encargoca.Ficha;
import coordinacion.procesos.cori.expediente.FichaExpedienteCorrectivo;
import org.monet.bpi.DatastoreService;
import org.monet.bpi.FieldComposite;
import org.monet.bpi.Source;
import org.monet.bpi.SourceService;
import org.monet.bpi.types.Date;
import org.monet.bpi.types.Link;
import org.monet.bpi.types.Term;

import java.util.ArrayList;

public class HechosOperacionesCorrectivas {


	public static Term obtenerOperacionRevisionPadre(Term operacion) {
		Source sourceControl = SourceService.get("coordinacion.ejecucion.TesauroOperaciones", null);
		return sourceControl.getParentTerm(operacion.getKey());

	}


	public static void anadirComponentesRevision(OperacionesRevisionDimension dimensionOperacionesRevision, Term operacion, Term padreNombre) {
		OperacionesRevisionDimensionComponent componenteOperacionesRevisionPadre = dimensionOperacionesRevision.insertComponent(padreNombre.getKey());
		rellenarComponenteOperacionesRevisionPadre(componenteOperacionesRevisionPadre, padreNombre);
		componenteOperacionesRevisionPadre.save();
		OperacionesRevisionDimensionComponent componenteOperacionesRevision = dimensionOperacionesRevision.insertComponent(operacion.getKey());
		rellenarComponenteOperacionesRevision(componenteOperacionesRevision, operacion, padreNombre);
		componenteOperacionesRevision.save();

	}

	public static void anadirComponentesTipoTrabajo(TipoTrabajoDimension dimensionTipoTrabajo, String trabajo) {
		TipoTrabajoDimensionComponent componenteTipoTrabajo = null;
		if (trabajo.equals("Correctivo")) {
			componenteTipoTrabajo = dimensionTipoTrabajo.insertComponent("001");
		} else {
			componenteTipoTrabajo = dimensionTipoTrabajo.insertComponent("002");
		}
		componenteTipoTrabajo.addNombre(trabajo);
		componenteTipoTrabajo.save();
	}

	public static void rellenarComponenteOperacionesRevisionPadre(OperacionesRevisionDimensionComponent componente, Term nombre) {
		componente.addNombre(nombre, new ArrayList<Term>());
	}


	public static void rellenarComponenteOperacionesRevision(OperacionesRevisionDimensionComponent componente, Term nombre, Term padreNombre) {
		ArrayList<Term> padres = crearJerarquiaOperaciones(padreNombre);
		componente.addNombre(nombre, padres);
	}

	public static ArrayList<Term> crearJerarquiaOperaciones(Term padreNombre) {
		ArrayList<Term> jerarquia = new ArrayList<Term>();
		jerarquia.add(padreNombre);
		return jerarquia;
	}

	public static void rellenarFactRevision(CuboOperacionesRevisionCubeFact fact, Link objeto, Term operacion, Link proveedor) {
		fact.setOperacionesRevision(operacion.getKey());
		fact.setObjetosActuacion(objeto.getId());
		fact.setTipoTrabajo("001");
		fact.setProveedores(proveedor.getId());
		fact.setMetrica(new Double(1));
	}

	public static void hechoOperacionRevisionCorrectivo(EncargoCA encargo) {
		Ficha ficha = encargo.getFicha();
		coordinacion.modulos.proveedores.Ficha fichaProveedor = Utilidad.obtenerProveedor(ficha.getCliente());
		Link proveedor = fichaProveedor.toLink();
		DataStoreIncidencia dataStoreInstance = (DataStoreIncidencia) DatastoreService
				.get("coordinacion.analytics.dashboardincidencias.DataStoreIncidencia");
		CuboOperacionesRevisionCube cuboOperacionesRevision = dataStoreInstance
				.getCuboOperacionesRevision();


		Coleccion encargos = (Coleccion) encargo.getOwner();
		Expediente expediente = (Expediente) encargos.getOwner();
		FichaExpedienteCorrectivo fichaExpediente = expediente.getFichaExpedienteCorrectivo();
		Link objeto = fichaExpediente.getObjetoActuacion();

		anadirComponentesTipoTrabajo(dataStoreInstance.getTipoTrabajo(), "Correctivo");

		for (FieldComposite operacion : ficha.getSeccionJerarquiaOperacionesField().getAllFields()) {
			SeccionJerarquiaOperaciones op = (SeccionJerarquiaOperaciones) operacion;
			Term hijo = op.getHijo();
			Term padre = op.getPadre();
			anadirComponentesRevision(dataStoreInstance.getOperacionesRevision(), hijo, padre);
			CuboOperacionesRevisionCubeFact fact = cuboOperacionesRevision.insertFact(new Date());
			rellenarFactRevision(fact, objeto, hijo, proveedor);
		}
		cuboOperacionesRevision.save();
	}

}
