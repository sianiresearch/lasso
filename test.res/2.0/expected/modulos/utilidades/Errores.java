package coordinacion.modulos.utilidades;

public class Errores {

	public static void imprimir(String sms, Exception exec) {
		if (exec != null) {
			exec.printStackTrace();
		}
		System.out.println(sms);

	}

}
