package coordinacion.modulos.utilidades;

import coordinacion.CatalogoObjetosActuacion;
import coordinacion.incidencias.Incidencia;
import coordinacion.procesos.nim.notificarincidencia.Schema;
import coordinacion.procesos.nim.notificarincidencia.schema.Datos;
import coordinacion.procesos.nim.notificarincidencia.schema.Gestion;
import org.monet.bpi.SensorResponse;
import org.monet.bpi.types.Link;
import org.monet.bpi.types.Location;
import org.monet.bpi.types.Picture;

public class NotificadorIncidencia {

	public static Incidencia notificacionIncidencia(SensorResponse msg) {
		Incidencia incidencia = Incidencia.createNew(coordinacion.procesos.nim.Coleccion.getInstance());
		incidencia.setLabel(incidencia.getLabel() + " mobile");
		Schema esquema = (coordinacion.procesos.nim.notificarincidencia.Schema) msg.getSchema();
		Datos paso1 = esquema.getDatos();
		Gestion paso2 = esquema.getGestion();
		if (paso1.getObjetoActuacion() != null) {
			Link enlaceObjeto = null;
			String codigoObjeto = paso1.getObjetoActuacion().getKey();
			CatalogoObjetosActuacion coleccionObjetos = coordinacion.CatalogoObjetosActuacion.getInstance();
			for (coordinacion.catalogoobjetosactuacion.Index reso : coleccionObjetos
					.get(coordinacion.catalogoobjetosactuacion.Index.Codigo.Eq(codigoObjeto))) {
				enlaceObjeto = reso.getIndexedNode().toLink();
			}
			incidencia.setObjetoActuacion(enlaceObjeto);
		}
		try {
			if (paso1.getImagen() != null) {
				for (Picture imagen : msg.getPictures(paso1.getImagen())) {
					incidencia.getImagenIncidenciaField().addNew(imagen);
				}
			}
			Utilidad.asignarMultiple(paso1.getTipoIncidencia(), incidencia.getTipoIncidenciaField());
			incidencia.setDescripcion(paso1.getDescripcion());
			if (paso1.getOrigen() != null) {
				incidencia.setOrigen(paso1.getOrigen());
			}
			incidencia.setFechaIncidencia(paso2.getFechaNotificacion());
			if (paso2.getPrioridad() != null) {
				incidencia.setPrioridad(paso2.getPrioridad());
			}
//			if (paso2.getResponsable() != null)
//				incidencia.setResponsable(paso2.getResponsable().getLabel());
			incidencia.setUrgente(paso2.isUrgente());
			Location localizacion = paso2.getPosicion();
			MapRequest mapRequest = new coordinacion.modulos.utilidades.MapRequest();
			incidencia.setImagenMapa(mapRequest.getMapaCoordenadas(
					"objeto actuación", localizacion.getGeometry()
							.getCentroid()));
			incidencia.setRegistroEntrada(paso2.getRegistroEntrada());
			incidencia.setRegistroSalida(paso2.getRegistroSalida());
			incidencia.save();
		} catch (Exception e) {
		}
		return incidencia;
	}
}
