package coordinacion.modulos.utilidades;

import org.monet.bpi.Expression;
import org.monet.bpi.Param;
import org.monet.bpi.types.Term;

import java.util.ArrayList;

public class Filtros {

	public static Expression filtrarEjecutadas(coordinacion.modulos.informes.incidencia.Ficha ficha) {
		Expression expresion = null;
		expresion = filtrarFecha(expresion, ficha.getFechaInicio(), coordinacion.incidencias.coleccion.Index.FechaEjecucion, true);
		expresion = filtrarFecha(expresion, ficha.getFechaFin(), coordinacion.incidencias.coleccion.Index.FechaEjecucion, false);

		if (ficha.getEstado() != null && ficha.getEstado().getLabel().equals("Cerrada"))
			expresion = filtrarTermEq(expresion, ficha.getEstado(), coordinacion.incidencias.coleccion.Index.Estado);

		if (ficha.getEstado() != null && !ficha.getEstado().getLabel().equals("Cerrada"))
			expresion = filtrarTermNe(expresion, new Term("e001", "Cerrada"), coordinacion.incidencias.coleccion.Index.Estado);

		expresion = filtrarTermEq(expresion, ficha.getLugar(), coordinacion.incidencias.coleccion.Index.LugarObjetoActuacion);
		expresion = filtrarTermEq(expresion, ficha.getZona(), coordinacion.incidencias.coleccion.Index.ZonaObjetoActuacion);
		expresion = filtrarTermEq(expresion, ficha.getResultadoEjecucion(), coordinacion.incidencias.coleccion.Index.ResultadoEjecucion);
		expresion = filtrarLink(expresion, ficha.getObjeto(), coordinacion.incidencias.coleccion.Index.ObjetoActuacion);
		return expresion;
	}

	public static Expression filtrarEnviadas(coordinacion.modulos.informes.incidencia.Ficha ficha) {
		Expression expresion = null;
		expresion = filtrarFecha(expresion, ficha.getFechaInicio(), coordinacion.incidencias.coleccion.Index.FechaEnvioOtac, true);
		expresion = filtrarFecha(expresion, ficha.getFechaFin(), coordinacion.incidencias.coleccion.Index.FechaEnvioOtac, false);

		expresion = filtrarTermEq(expresion, ficha.getLugar(), coordinacion.incidencias.coleccion.Index.LugarObjetoActuacion);
		expresion = filtrarTermEq(expresion, ficha.getZona(), coordinacion.incidencias.coleccion.Index.ZonaObjetoActuacion);
		expresion = filtrarLink(expresion, ficha.getObjeto(), coordinacion.incidencias.coleccion.Index.ObjetoActuacion);

		expresion = filtrarTermEq(expresion, new Term("e006", "Corrigiendo"), coordinacion.incidencias.coleccion.Index.Estado);

		expresion = filtrarUnidad(expresion, ficha.getUnidad(), coordinacion.incidencias.coleccion.Index.Unidad);
		return expresion;
	}

	private static boolean esPrimerFiltro(Expression expresion) {
		return (expresion == null);
	}

	public static Expression filtrarUnidad(Expression expresion, Term unidad, Param parametro) {
		boolean primerFiltro = esPrimerFiltro(expresion);
		Expression expresionMultiple = null;

		if (unidad != null && unidad.getKey() != null) {
			if (unidad.getKey().equals("u001"))
				expresionMultiple = Expression.Or(parametro.Eq("Ejecucion"), parametro.Eq("ejecucion"));

			return (primerFiltro) ? expresionMultiple : Expression.And(expresion, expresionMultiple);
		}
		return expresion;
	}

	public static Expression filtrarFecha(Expression expresion, org.monet.bpi.types.Date fecha, Param parametro, boolean mayor) {
		boolean primerFiltro = esPrimerFiltro(expresion);

		if (fecha != null && primerFiltro)
			return (mayor) ? parametro.Ge(fecha.getValue()) : parametro.Le(fecha.getValue());

		if (fecha != null && !primerFiltro)
			return (mayor) ? Expression.And(expresion, parametro.Ge(fecha.getValue())) :
					Expression.And(expresion, parametro.Le(fecha.plusDays(1).getValue()));

		return expresion;
	}

	public static Expression filtrarLink(Expression expresion, org.monet.bpi.types.Link enlace, Param parametro) {
		boolean primerFiltro = esPrimerFiltro(expresion);

		if (enlace != null && primerFiltro)
			return parametro.Eq(enlace);

		if (enlace != null && !primerFiltro)
			return Expression.And(expresion, parametro.Eq(enlace));

		return expresion;
	}

	public static Expression filtrarTermEq(Expression expresion, Term termino, Param parametro) {
		boolean primerFiltro = esPrimerFiltro(expresion);

		if (termino != null && termino.getKey() != null && primerFiltro)
			return parametro.Eq(termino.getLabel());

		if (termino != null && termino.getKey() != null && !primerFiltro)
			return Expression.And(expresion, parametro.Eq(termino.getLabel()));

		return expresion;
	}

	public static Expression filtrarTermNe(Expression expresion, Term termino, Param parametro) {
		boolean primerFiltro = esPrimerFiltro(expresion);

		if (termino != null && termino.getKey() != null && primerFiltro)
			return parametro.Ne(termino.getLabel());

		if (termino != null && termino.getKey() != null && !primerFiltro)
			return Expression.And(expresion, parametro.Ne(termino.getLabel()));

		return expresion;
	}

	public static Expression filtrarMultiplesTerminos(Expression expresion, ArrayList<Term> tiposFiltrados, Param parametro) {
		boolean primerFiltro = esPrimerFiltro(expresion);
		Expression expresionMultiple = null;
		if (tiposFiltrados != null && tiposFiltrados.size() > 0) {
			expresionMultiple = parametro.Eq(tiposFiltrados.get(0).getLabel());
			for (Term tipo : tiposFiltrados)
				expresionMultiple = Expression.Or(expresionMultiple, parametro.Eq(tipo.getLabel()));

			return (primerFiltro) ? expresionMultiple : Expression.And(expresion, expresionMultiple);
		}
		return expresion;
	}
}
