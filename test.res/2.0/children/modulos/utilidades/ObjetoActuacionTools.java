package coordinacion.modulos.utilidades;

import coordinacion.objetosactuacion.ObjetoActuacion;
import coordinacion.objetosactuacion.objetoactuacion.ficha.Localizacion;
import org.monet.bpi.Node;
import org.monet.bpi.types.Term;

public class ObjetoActuacionTools {

	public static String obtenerPadreLabel(Node objeto) {
		while (objeto != null) {
			if (objeto instanceof coordinacion.objetosactuacion.ObjetoActuacion) {
				coordinacion.objetosactuacion.objetoactuacion.Ficha ficha = ((coordinacion.objetosactuacion.ObjetoActuacion) objeto)
						.getFicha();
				return ficha.getNombre();
			}
			objeto = objeto.getOwner();
		}
		return null;
	}

	public static ObjetoActuacion obtenerPadreNode(Node objeto) {
		while (objeto != null) {
			if (objeto instanceof coordinacion.objetosactuacion.ObjetoActuacion) {
				coordinacion.objetosactuacion.ObjetoActuacion objetoActuacion = (coordinacion.objetosactuacion.ObjetoActuacion) objeto;
				return objetoActuacion;
			}
			objeto = objeto.getOwner();
		}
		return null;
	}

	public static String obtenerLugar(Node objeto) {
		ObjetoActuacion padre = obtenerPadreNode(objeto);
		coordinacion.objetosactuacion.objetoactuacion.Ficha ficha = padre.getFicha();
		Localizacion localizacion = (Localizacion) ficha.getLocalizacionField();
		Object tipoLugar = localizacion.getLugar();
		String lugar = null;
		if (tipoLugar instanceof String) {
			lugar = (String) tipoLugar;
		} else {
			if (tipoLugar instanceof Term) {
				Term termLugar = (Term) tipoLugar;
				lugar = termLugar.getLabel();
			}
		}
		return lugar;
	}

	public static String obtenerZona(Node objeto) {
		ObjetoActuacion padre = obtenerPadreNode(objeto);
		coordinacion.objetosactuacion.objetoactuacion.Ficha ficha = padre.getFicha();
		Localizacion localizacion = (Localizacion) ficha.getLocalizacionField();
		Object tipoZona = localizacion.getZona();
		String zona = null;
		if (tipoZona instanceof String) {
			zona = (String) tipoZona;
		} else {
			if (tipoZona instanceof Term) {
				Term termZona = (Term) tipoZona;
				zona = termZona.getLabel();
			}
		}
		return zona;
	}

}
