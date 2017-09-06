package coordinacion.modulos.utilidades;

import org.monet.bpi.FieldComposite;

public class ControlCalidad {

	public static void configuracion() {

//		Source source = SourceService.get("", null);
//		source.


	}

	public static void calcularMultiple(coordinacion.modulos.controlcalidad.Ficha ficha, coordinacion.procesos.coca.facturacion.Schema item) {

		coordinacion.modulos.controlcalidad.Contenedor controlCalidad = coordinacion.modulos.controlcalidad.Contenedor.getInstance();
		coordinacion.modulos.controlcalidad.Configuracion configuracion = controlCalidad.getConfiguracion();
		item.setFechaCertificacion(ficha.getFechaCertificacion());
		item.setNombreTecnico(ficha.getTecnico());
		item.setPorcentajeSobreVariable(Utilidad.EconomicNumberString(configuracion.getPorcentajeVariable().doubleValue()));
		Double cuantiaMaxTotal = ficha.getCuantiaMaximaTotal().doubleValue();
		item.setCuantiaMaxTotal(Utilidad.EconomicNumberString(cuantiaMaxTotal));
		Double totalPercibir = 0.0;
		//Double totalPercibir = cuantia1 + cuantia2 + cuantia3+ cuantia4;		

		for (FieldComposite indicador : ficha.getIndicadoresField().getAllFields()) {
			coordinacion.modulos.controlcalidad.ficha.Indicadores indicadorFicha = (coordinacion.modulos.controlcalidad.ficha.Indicadores) indicador;
			for (FieldComposite config : configuracion.getSeccionIndicadoresField().getAllFields()) {
				coordinacion.modulos.controlcalidad.configuracion.SeccionIndicadores indicadorConfig = (coordinacion.modulos.controlcalidad.configuracion.SeccionIndicadores) config;
				if (indicadorFicha.getNombre().getKey().equals(indicadorConfig.getNombre().getKey())) {
					coordinacion.procesos.coca.facturacion.schema.CalculoIndicadores itemCalculo = new coordinacion.procesos.coca.facturacion.schema.CalculoIndicadores();

					Double calculo = (indicadorConfig.getPorcentaje().doubleValue() / 100) * cuantiaMaxTotal;
					Double cuantia = 0.0;
					if (indicadorConfig.getTipoPonderacion().getKey().equals("tp001")) {
						cuantia = (1 - (indicadorFicha.getIndicador().doubleValue() / 100)) * calculo;
					} else {
						cuantia = (indicadorFicha.getIndicador().doubleValue() / 100) * calculo;
					}
					itemCalculo.setCuantiaMax(Utilidad.EconomicNumberString(calculo));
					itemCalculo.setPorcentaje(Utilidad.EconomicNumberString(indicadorFicha.getIndicador().doubleValue()));
					itemCalculo.setPercibir(Utilidad.EconomicNumberString(cuantia));
					itemCalculo.setPorcentajeVariable(Utilidad.EconomicNumberString(indicadorConfig.getPorcentaje().doubleValue()));
					itemCalculo.setNombre(indicadorConfig.getNombre());
					itemCalculo.setTipo(indicadorConfig.getTipoPonderacion());
					totalPercibir = totalPercibir + cuantia;
					item.getCalculoIndicadores().add(itemCalculo);
				}
			}
		}
		Double totalImpuestos = totalPercibir * 0.07;
		Double total = totalPercibir - totalImpuestos;
		item.setPercibirTotal(Utilidad.EconomicNumberString(totalPercibir));
		item.setTotalImpuestos(Utilidad.EconomicNumberString(totalImpuestos));
		item.setTotal(Utilidad.EconomicNumberString(total));

	}

	public static void calcular(coordinacion.modulos.controlcalidad.Ficha ficha, coordinacion.procesos.coca.facturacion.Schema item) {

//   		Double cuantiaMaxTotal = ficha.getCuantiaMaximaTotal().doubleValue();
//		item.setCuantiaMaxTotal(Utilidad.EconomicNumberString(cuantiaMaxTotal));
//		Double calculo1 = 0.30 * cuantiaMaxTotal;
//		Double cuantia1 = (1-(ficha.getIndicador1().doubleValue() / 100))* calculo1;
//		item.setCuantiaMax1(Utilidad.EconomicNumberString(calculo1));
//		item.setPorcentaje1(Utilidad.EconomicNumberString(ficha.getIndicador1().doubleValue()));
//		item.setPercibir1(Utilidad.EconomicNumberString(cuantia1));
//		
//		Double calculo2 = 0.30 * cuantiaMaxTotal;
//		Double cuantia2 = (ficha.getIndicador2().doubleValue() / 100)* calculo2;
//		item.setCuantiaMax2(Utilidad.EconomicNumberString(calculo2));
//		item.setPorcentaje2(Utilidad.EconomicNumberString(ficha.getIndicador2().doubleValue()));
//		item.setPercibir2(Utilidad.EconomicNumberString(cuantia2));
//		
//		Double calculo3 = 0.20 * cuantiaMaxTotal;
//		Double cuantia3 = (ficha.getIndicador3().doubleValue() / 100)* calculo3;
//		item.setCuantiaMax3(Utilidad.EconomicNumberString(calculo3));
//		item.setPorcentaje3(Utilidad.EconomicNumberString(ficha.getIndicador3().doubleValue()));
//		item.setPercibir3(Utilidad.EconomicNumberString(cuantia3));
//		
//		Double calculo4 = 0.20 * cuantiaMaxTotal;
//		Double cuantia4 = (ficha.getIndicador4().doubleValue() / 100)* calculo4;
//		item.setCuantiaMax4(Utilidad.EconomicNumberString(calculo4));
//		item.setPorcentaje4(Utilidad.EconomicNumberString(ficha.getIndicador4().doubleValue()));
//		item.setPercibir4(Utilidad.EconomicNumberString(cuantia4));
//		
//				
//		Double totalPercibir = cuantia1 + cuantia2 + cuantia3+ cuantia4;
//		Double totalImpuestos = totalPercibir * 0.07;
//		Double total = totalPercibir - totalImpuestos;
//		
//		item.setPercibirTotal(Utilidad.EconomicNumberString(totalPercibir));
//		item.setTotalImpuestos(Utilidad.EconomicNumberString(totalImpuestos));
//		item.setTotal(Utilidad.EconomicNumberString(total));
//		
//		item.setFechaCertificacion(ficha.getFechaCertificacion());
//		item.setNombreTecnico(ficha.getTecnico());		

	}

}
