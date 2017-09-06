package coordinacion.modulos.utilidades;

import coordinacion.modulos.componentes.ficha.Ubicacion;
import coordinacion.objetosactuacion.ObjetoActuacion;
import coordinacion.objetosactuacion.objetoactuacion.ficha.Localizacion;
import org.monet.bpi.BusinessUnit;
import org.monet.bpi.Node;
import org.monet.bpi.NodeService;
import org.monet.bpi.types.Date;
import org.monet.bpi.types.Picture;

import java.util.Iterator;

public class OrdenTrabajoComun {

	public static void exportarEncargo(
			coordinacion.modulos.encargos.encargo.Ficha ficha,
			coordinacion.procesos.cori.otc.Schema item) {
		item.setCodigo(ficha.getCodigo());
		item.setPrioridad(ficha.getPrioridad());
		item.setFechaInicio(ficha.getFechaInicio());
		item.setFechaFinLimite(ficha.getFechaFinLimite());
		item.setObservaciones(ficha.getObservaciones());
		item.setAdjuntos(ficha.getAdjuntos());
	}

	public static void exportarObjetosActuacion(
			coordinacion.incidencias.Incidencia incidencia,
			coordinacion.procesos.cori.otc.Schema item) {
		Node objeto = NodeService.get(incidencia.getObjetoActuacion().getId());
		if (objeto == null)
			return;
		ObjetoActuacion objetoActuacion = ObjetoActuacionTools
				.obtenerPadreNode(objeto);
		coordinacion.objetosactuacion.objetoactuacion.Ficha ficha = objetoActuacion
				.getFicha();
		item.getObjetoSyn().setCodigoTarget(ficha.getCodigo());
		item.getObjetoSyn().setCodigo(ficha.getCodigo());
		item.getObjetoSyn().setImagen(ficha.getImagen());
		item.getObjetoSyn().setNombre(ficha.getNombre());
		item.getObjetoSyn().setIdExtranjera(objetoActuacion.toLink().getId());
		Localizacion localizacion = (Localizacion) ficha.getLocalizacionField();
		if (localizacion.getLugar() != null) {
			String lugar = localizacion.getLugar().getLabel();
			item.setLugar(lugar);
			item.getObjetoSyn().setLugar(lugar);
		}
		if (localizacion.getZona() != null) {
			String zona = localizacion.getZona().getLabel();
			item.setZona(zona);
			item.getObjetoSyn().setZona(zona);
		}
		item.setDireccion(localizacion.getDireccion());
		item.setTelefono(localizacion.getTelefono());
		if (objeto instanceof coordinacion.modulos.componentes.coleccion.Componente) {
			coordinacion.modulos.componentes.coleccion.Componente componente = (coordinacion.modulos.componentes.coleccion.Componente) objeto;
			coordinacion.modulos.componentes.Ficha fichaComponente = componente
					.getFicha();
			item.getObjetoSyn().setCodigoTarget(fichaComponente.getCodigo());
			item.getObjetoSyn().getComponente()
					.setCodigo(fichaComponente.getCodigo());
			item.getObjetoSyn().getComponente()
					.setNombre(fichaComponente.getNombre());
			item.getObjetoSyn().getComponente()
					.setIdExtranjera(componente.toLink().getId());
			Ubicacion ubicacion = (Ubicacion) fichaComponente.getUbicacionField();
			item.getObjetoSyn().getComponente().setLugar(ubicacion.getLugar());
			item.getObjetoSyn().getComponente().setZona(ubicacion.getZona());
			item.getObjetoSyn().getComponente().setImagen(fichaComponente.getImagen());
		}
	}

	public static void exportarOtros(coordinacion.procesos.cori.otc.Schema item) {
		item.setCliente(BusinessUnit.getName());
	}

	public static void exportarIncidencia(
			coordinacion.incidencias.Incidencia incidencia,
			coordinacion.procesos.cori.otc.Schema item) {

		item.setIdIncidencia(incidencia.getCodigoIncidencia());
		item.setCodigoNotificacionIncidencia(incidencia.getCodigoNotificacionIncidencia());
		item.setObjetoActuacion(incidencia.getObjetoActuacionAsTerm());
		item.setEdificio(incidencia.getObjetoPadre());
		item.setOrigen(incidencia.getOrigen());
		item.setFechaIncidencia(incidencia.getFechaIncidencia());
		item.getTipoIncidencia().addAll(
				incidencia.getTipoIncidenciaField().getAll());
		item.setTipoIncidenciaLabel(incidencia.getTipoIncidenciaField()
				.toString());
		item.setDescripcion(incidencia.getDescripcion());
		if (incidencia.getImagenIncidencia().size() > 0) {
			coordinacion.procesos.cori.otc.schema.VisorImagenIncidencia imagenIncidencia = new coordinacion.procesos.cori.otc.schema.VisorImagenIncidencia();
			imagenIncidencia.setImagenIncidencia(incidencia.getImagenIncidenciaField().get(0));
			item.getVisorImagenIncidencia().add(imagenIncidencia);
		}

		Iterator<Picture> iterador = incidencia.getImagenIncidencia()
				.iterator();
		if (iterador.hasNext()) {
			iterador.next();
		}
		while (iterador.hasNext()) {
			coordinacion.procesos.cori.otc.schema.Anexo fila = new coordinacion.procesos.cori.otc.schema.Anexo();
			fila.setImagen1(iterador.next());
			if (iterador.hasNext()) {
				fila.setImagen2(iterador.next());
			}
			item.getAnexo().add(fila);
		}
		item.setAdjuntosIncidencia(incidencia.getAdjuntosIncidenciaField()
				.getAll());
		item.setUrgente(incidencia.getUrgente());
		if (incidencia.getImagenMapa() != null) {
			coordinacion.procesos.cori.otc.schema.VisorMapa mapa = new coordinacion.procesos.cori.otc.schema.VisorMapa();
			mapa.setImagenMapa(incidencia.getImagenMapa());
			item.getVisorMapa().add(mapa);
		}
		incidencia.setFechaUltimaOt(new Date());
		incidencia.save();

	}

}
