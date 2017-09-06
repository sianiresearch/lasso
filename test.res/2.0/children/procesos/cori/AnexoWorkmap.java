package coordinacion.procesos.cori;

import coordinacion.Expedientes;
import coordinacion.incidencias.Incidencia;
import coordinacion.modulos.asistentes.ConfigurarEncargoCorrectivo;
import coordinacion.modulos.utilidades.HechosIncidencia;
import coordinacion.modulos.utilidades.Utilidad;
import coordinacion.procesos.cori.Expediente;
import coordinacion.procesos.cori.Workmap;
import coordinacion.procesos.cori.documentos.Otic;
import coordinacion.procesos.cori.documentos.Ptac;
import coordinacion.procesos.cori.documentos.ptac.Schema;
import coordinacion.procesos.cori.encargos.EncargoCA;
import coordinacion.procesos.cori.encargos.EncargoCI;
import coordinacion.procesos.cori.encargos.EncargoCV;
import coordinacion.procesos.cori.expediente.FichaExpedienteCorrectivo;
import coordinacion.procesos.cori.ptc.schema.Anexo;
import coordinacion.procesos.cori.workmap.Place;
import org.monet.bpi.*;
import org.monet.bpi.types.Date;
import org.monet.bpi.types.Link;
import org.monet.bpi.types.Picture;
import org.monet.bpi.types.Term;

import java.util.ArrayList;

public class AnexoWorkmap {

	public static void setupComplete(Incidencia incidencia, String providerLabel, coordinacion.modulos.encargos.encargo.Ficha encargo) {
		encargo.setCliente(providerLabel);
		encargo.save();
		incidencia.setUnidad(Utilidad.providerLabel(providerLabel));
		String proveedor = Utilidad.providerLabel(providerLabel);
		incidencia.save();
		Utilidad.anadirProveedor(proveedor);
	}

	public static void inicializar(coordinacion.procesos.cori.Workmap tarea) {
		Expediente expediente = Expediente.createNew(Expedientes.getInstance());
		tarea.setTarget(expediente);
		FichaExpedienteCorrectivo ficha = expediente
				.getFichaExpedienteCorrectivo();
		Incidencia incidencia = (Incidencia) tarea.getShIncidencia();
		String codigoI = incidencia.getCodigoIncidencia();
		String codigoE = ficha.getCodigo();
		Link objetoActuacion = incidencia.getObjetoActuacion();
		String objetoLabel = "";
		FieldMultiple<FieldSelect, Term> tipoIncidencia = incidencia
				.getTipoIncidenciaField();
		boolean urgente = incidencia.getUrgente();
		tarea.setFlag("urgente", String.valueOf(urgente));
		String tipos = "";
		if (tipoIncidencia != null) {
			tipos = tipoIncidencia.toString();
		}
		if (objetoActuacion != null) {
			objetoLabel = objetoActuacion.getLabel();
		}
		ficha.setPrioridad(incidencia.getPrioridad());
		ficha.setFechaApertura(new Date());
		ficha.setObjetoActuacion(objetoActuacion);
		ficha.setEnlaceIncidencia(incidencia.toLink());
		if (incidencia.getFlag("corregida") != null) {
			AnexoWorkmap.incidenciaCorregida(tarea, expediente, incidencia);
			ficha.setEstado(new Term("corregida", "Ejecutado"));
		} else {
			ficha.setEstado(new Term("programado", "Programado"));
		}
		ficha.save();
		tarea.setLabel(BusinessUnit.getName() + " " + codigoI + " "
				+ objetoLabel + " " + tipos + " " + codigoE);
		tarea.save();

	}

	public static void incidenciaCorregida(Workmap tarea,
										   Expediente expediente, Incidencia incidencia) {
		EncargoCA encargo = EncargoCA.createNew(expediente.getColeccion());
		coordinacion.procesos.cori.encargos.encargoca.Ficha fichaEncargo = encargo
				.getFicha();
		FichaExpedienteCorrectivo fichaExpediente = expediente
				.getFichaExpedienteCorrectivo();
		int codigo = 0;
		if (tarea.getFlag("codigoEA") != null) {
			codigo = Integer.parseInt(tarea.getFlag("codigoEA")) + 1;
		} else {
			codigo = 1;
		}
		tarea.setFlag("codigoEA", String.valueOf(codigo));
		fichaEncargo.setCodigo(fichaExpediente.getCodigo() + " A-" + codigo);
		fichaEncargo.setFechaInicio(incidencia.getFechaIncidencia());
		fichaEncargo.setFechaFinLimite(incidencia.getFechaIncidencia());
		fichaEncargo.setObservaciones(incidencia.getDescripcion());
		fichaEncargo.setPrioridad(incidencia.getPrioridad());
		fichaEncargo.setFechaRecepcionPT(new Date());
		Ptac parte = (Ptac) tarea.getShParteIncidenciaCorregida();
		fichaEncargo.setPt(parte.toLink());
		Schema esquema = parte.getSchema();
		fichaEncargo.setFechaInicioReal(esquema.getFechaInicioReal());
		fichaEncargo.setFechaFinReal(esquema.getFechaFinReal());
		fichaEncargo.setResultado(esquema.getResultado());
		fichaEncargo.setImagenResultado(esquema.getImagenResultado());
		for (Anexo anexo : esquema.getAnexo()) {
			if (anexo.getImagen1() != null) {
				fichaEncargo.getImagenesAnexoField().addNew(anexo.getImagen1());
			}
			if (anexo.getImagen2() != null) {
				fichaEncargo.getImagenesAnexoField().addNew(anexo.getImagen2());
			}
		}
		fichaEncargo.setObservacionesResultado(esquema
				.getObservacionesResultado());
		Utilidad.asignarMultiple(esquema.getAdjuntosResultado(), fichaEncargo.getAdjuntosResultadoField());
		if (esquema.getCalculoCoste() != null)
			fichaEncargo.setCalculoCoste(esquema.getCalculoCoste());
		if (esquema.getCalculoCosteFacturable() != null)
			fichaEncargo.setCalculoCosteFacturable(esquema
					.getCalculoCosteFacturable());
		fichaEncargo.save();
		tarea.setShEncargoCA(encargo);
		tarea.save();
		incidencia.setResultadoEjecucion("Corregida");
		incidencia.setEstado(new Term("e005", "Ejecutada"));
		incidencia.setFechaModificacion(new Date());
		incidencia.setFechaEjecucion(esquema.getFechaFinReal());
		incidencia.save();
		HechosIncidencia.hechoIncidenciaCorregida(incidencia);
	}

	public static void abortarExpediente(Workmap tarea) {
		Expediente expediente = tarea.getTarget();
		if (expediente != null) {
			FichaExpedienteCorrectivo ficha = expediente
					.getFichaExpedienteCorrectivo();
			ficha.setEstado(new Term("cancelada", "Cancelado"));
			ficha.save();
		}
		if (tarea.getShIncidencia() != null) {
			Incidencia incidencia = (Incidencia) tarea.getShIncidencia();
			incidencia.setEstado(new Term("e009", "Cancelada"));
			incidencia.setFechaModificacion(new Date());
			incidencia.save();
			incidencia.lock();
			HechosIncidencia.hechoIncidenciaCancelada(incidencia);
		}
		tarea.doGoto(Place.Finalizar, "Tarea abortada");

	}

	public static void cerrarExpediente(Workmap tarea) {
		Expediente expediente = tarea.getTarget();
		FichaExpedienteCorrectivo ficha = expediente
				.getFichaExpedienteCorrectivo();
		ficha.setFechaArchivo(new Date());
		ficha.setEstado(new Term("cerrado", null));
		ficha.save();
		Incidencia incidencia = (Incidencia) tarea.getShIncidencia();
		incidencia.setEstado(new Term(null, "Cerrada"));
		incidencia.setFechaCierre(new Date());
		incidencia.setFechaModificacion(new Date());
		incidencia.save();
		incidencia.lock();
		HechosIncidencia.hechoIncidenciaCerrada(incidencia);
		tarea.doGoto(Place.Finalizar, "Expediente cerrado");
	}

	public static void solveConfiguracionEncargoInspeccion(Workmap tarea,
														   String tipoEncargo, ConfigurarEncargoCorrectivo f) {
		Expediente expediente = tarea.getTarget();
		EncargoCI encargo;
		if (tarea.getShEncargoCI() == null) {
			encargo = EncargoCI.createNew(expediente.getColeccion());
			tarea.setShEncargoCI(encargo);
			tarea.save();
		} else {
			encargo = (EncargoCI) tarea.getShEncargoCI();
		}
		FichaExpedienteCorrectivo fichaExpediente = expediente
				.getFichaExpedienteCorrectivo();
		coordinacion.procesos.cori.encargos.encargoci.Ficha fichaEncargo = encargo
				.getFicha();
		int codigo = 0;
		if (tarea.getFlag("codigoEI") != null) {
			codigo = Integer.parseInt(tarea.getFlag("codigoEI")) + 1;
		} else {
			codigo = 1;
		}
		tarea.setFlag("codigoEI", String.valueOf(codigo));
		fichaEncargo.setCodigo(fichaExpediente.getCodigo() + " I-" + codigo);
		fichaEncargo.setFechaInicio(f.getFechaInicio());
		fichaEncargo.setFechaFinLimite(f.getFechaFinLimite());
		fichaEncargo.setObservaciones(f.getObservaciones());
		fichaEncargo.setPrioridad(fichaExpediente.getPrioridad());
		Utilidad.asignarMultiple(f.getAdjuntos(), fichaEncargo.getAdjuntosField());
		fichaEncargo.save();

		Otic orden = coordinacion.procesos.cori.documentos.otic.Exportador.doExportOf(fichaEncargo).toNewDocument();
		AnexoWorkmap.cambiarEstadoIncidencia((Incidencia) tarea.getShIncidencia(), new Term(null, "Inspeccionando"));
		orden.consolidate();
		fichaEncargo.setFechaEnvioOT(new Date());
		fichaEncargo.setOt(orden.toLink());
		fichaEncargo.save();
		ArrayList<MonetLink> links = new ArrayList<MonetLink>();
		links.add(orden.toMonetLink());
		tarea.addLog("Orden de trabajo generada", "Orden de trabajo de verificación", links);
		tarea.setShEncargoCI(encargo);
		tarea.save();
	}


	public static boolean verificarInternaJob(EncargoCV encargo, JobResponse msg) {
		coordinacion.procesos.cori.jobs.verificar.Schema esquema = (coordinacion.procesos.cori.jobs.verificar.Schema) msg.getSchema();
		coordinacion.procesos.cori.encargos.encargocv.Ficha ficha = encargo.getFicha();
		coordinacion.procesos.cori.jobs.verificar.schema.Resultado resultado = esquema.getResultado();
		if (resultado.getImagen() != null) {
			int i = 0;
			for (Picture imagen : msg.getPictures(resultado.getImagen())) {
				if (i == 0) {
					ficha.setImagenResultado(imagen);
				} else {
					ficha.getImagenesAnexoField().addNew(imagen);

				}

				i++;
			}
		}
		ficha.setObservacionesResultado(resultado.getObservaciones());

		coordinacion.procesos.cori.jobs.verificar.schema.Resultado2 resultado2 = esquema.getResultado2();
		ficha.setFechaInicioReal(resultado2.getFechaInicio());
		ficha.setFechaFinReal(resultado2.getFechaFin());
		if (resultado2.getFirmaOperario() != null) {
//			ficha.setFirmaOperario(msg.getPicture(resultado2.getFirmaOperario()));
		}
		ficha.setResultado(resultado2.isVerificado());
		ficha.save();
		return esquema.getResultado2().isVerificado();
	}


	public static boolean inspeccionarInternaJob(EncargoCI encargo, JobResponse msg) {
		coordinacion.procesos.cori.jobs.inspeccionar.Schema esquema = (coordinacion.procesos.cori.jobs.inspeccionar.Schema) msg.getSchema();
		coordinacion.procesos.cori.encargos.encargoci.Ficha ficha = encargo.getFicha();
		coordinacion.procesos.cori.jobs.inspeccionar.schema.Resultado resultado = esquema.getResultado();
		if (resultado.getImagen() != null) {
			int i = 0;
			for (Picture imagen : msg.getPictures(resultado.getImagen())) {
				if (i == 0) {
					ficha.setImagenResultado(imagen);
				} else {
					ficha.getImagenesAnexoField().addNew(imagen);
				}

				i++;
			}
		}
		ficha.setObservacionesResultado(resultado.getObservaciones());
		coordinacion.procesos.cori.jobs.inspeccionar.schema.Resultado2 resultado2 = esquema.getResultado2();
		ficha.setFechaInicioReal(resultado2.getFechaInicio());
		ficha.setFechaFinReal(resultado2.getFechaFin());
		if (resultado2.getFirmaOperario() != null) {
//			ficha.setFirmaOperario(msg.getPicture(resultado2.getFirmaOperario()));
		}
		ficha.setResultado(resultado2.isInspeccionada());
		ficha.save();
		return esquema.getResultado2().isInspeccionada();
	}

	public static void cambiarEstadoIncidencia(Incidencia incidencia, Term estado) {
		incidencia.setEstado(estado);
		incidencia.save();
	}

	public static void rellenarFichaEncargoJobCV(EncargoCV encargo, Expediente expediente, String tipo,
												 int codigo, Date fechaInicio, Date fechaFinLimite, String observaciones) {
		coordinacion.modulos.encargos.encargo.Ficha fichaEncargo = encargo.getFicha();
		FichaExpedienteCorrectivo fichaExpediente = expediente.getFichaExpedienteCorrectivo();

		fichaEncargo.setCodigo(fichaExpediente.getCodigo() + tipo + codigo);
		fichaEncargo.setPrioridad(fichaExpediente.getPrioridad());
		fichaEncargo.setFechaInicio(fechaInicio);
		fichaEncargo.setFechaFinLimite(fechaFinLimite);
		fichaEncargo.setObservaciones(observaciones);
		fichaEncargo.save();
	}

	public static void rellenarFichaEncargoJobCI(EncargoCI encargo, Expediente expediente, String tipo,
												 int codigo, Date fechaInicio, Date fechaFinLimite, String observaciones) {
		coordinacion.modulos.encargos.encargo.Ficha fichaEncargo = encargo.getFicha();
		FichaExpedienteCorrectivo fichaExpediente = expediente.getFichaExpedienteCorrectivo();

		fichaEncargo.setCodigo(fichaExpediente.getCodigo() + tipo + codigo);
		fichaEncargo.setPrioridad(fichaExpediente.getPrioridad());
		fichaEncargo.setFechaInicio(fechaInicio);
		fichaEncargo.setFechaFinLimite(fechaFinLimite);
		fichaEncargo.setObservaciones(observaciones);
		fichaEncargo.save();
	}

}
