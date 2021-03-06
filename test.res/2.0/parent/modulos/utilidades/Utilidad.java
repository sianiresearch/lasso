package coordinacion.modulos.utilidades;

import coordinacion.modulos.proveedores.Coleccion;
import coordinacion.modulos.proveedores.Ficha;
import org.monet.bpi.*;
import org.monet.bpi.types.File;
import org.monet.bpi.types.Picture;
import org.monet.bpi.types.Term;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;


public class Utilidad {

	public static int aleatorio(int longitud) {
		Random rn = new Random();
		Integer elegido = rn.nextInt(longitud - 1);
		return elegido + 1;
	}

	public static org.monet.bpi.types.Number EconomicNumber(Double value) {
		DecimalFormat formato = new DecimalFormat("###,###.00");
		String salida = formato.format(value);
		org.monet.bpi.types.Number result = new org.monet.bpi.types.Number(value, salida);
		return result;
	}

	public static String EconomicNumberString(Double value) {
		DecimalFormat formato = new DecimalFormat("###,###.00");
		String salida = formato.format(value);
		String result = String.valueOf(new org.monet.bpi.types.Number(value, salida));
		return result;
	}

	public static String providerLabel(String providerLabel) {
		if (providerLabel == null)
			return null;
		String proveedor = providerLabel;
		if (providerLabel.indexOf("-") > 0)
			proveedor = providerLabel.substring(0,
					providerLabel.indexOf("-") - 1);
		return proveedor;
	}

	public static void anadirProveedor(String proveedorServicio) {
		String proveedor = providerLabel(proveedorServicio);
		Coleccion proveedores = coordinacion.modulos.proveedores.Coleccion.getInstance();
		for (coordinacion.modulos.proveedores.coleccion.Index pro : proveedores.get(coordinacion.modulos.proveedores.coleccion.Index.Nombre.Eq(proveedor))) {
			return;
		}
		Ficha ficha = Ficha.createNew(proveedores);
		ficha.setNombre(proveedor);
		ficha.save();
	}

	public static Ficha obtenerProveedor(String proveedorServicio) {
		String proveedor = providerLabel(proveedorServicio);
		Coleccion proveedores = coordinacion.modulos.proveedores.Coleccion.getInstance();
		for (coordinacion.modulos.proveedores.coleccion.Index pro : proveedores.get(coordinacion.modulos.proveedores.coleccion.Index.Nombre.Eq(proveedor))) {
			return (Ficha) pro.getIndexedNode();
		}
		Ficha ficha = Ficha.createNew(proveedores);
		ficha.setNombre(proveedor);
		ficha.save();
		return ficha;
	}

	public static void asignarMultiple(ArrayList<?> origen, FieldMultiple<?, ?> destino) {
		for (Object elemento : origen) {
			if (elemento != null) {
				if (elemento instanceof Term) {
					FieldMultiple<FieldSelect, Term> destinoReal = (FieldMultiple<FieldSelect, Term>) destino;
					destinoReal.addNew((Term) elemento);

				} else {
					if (elemento instanceof File) {
						FieldMultiple<FieldFile, File> destinoReal = (FieldMultiple<FieldFile, File>) destino;
						destinoReal.addNew((File) elemento);
					} else {
						if (elemento instanceof Picture) {
							FieldMultiple<FieldPicture, Picture> destinoReal = (FieldMultiple<FieldPicture, Picture>) destino;
							destinoReal.addNew((Picture) elemento);

						} else {
							if (elemento instanceof String) {
								FieldMultiple<FieldText, String> destinoReal = (FieldMultiple<FieldText, String>) destino;
								destinoReal.addNew((String) elemento);
							}
						}
					}
				}
			}
		}
	}

}
