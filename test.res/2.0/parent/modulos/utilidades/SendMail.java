package coordinacion.modulos.utilidades;

import org.monet.bpi.MailService;
import org.monet.bpi.Task;

import java.util.ArrayList;

public class SendMail {

	public static void sendNotification(String message, Task tarea) {
		// arg0: to
		// arg1: subject
		// arg2: contenido (si html aqui el html y en arg3 el texto plano)
		// arg3: si es html texto plano, si no file
		try {
			coordinacion.Preferencias ficha = coordinacion.Preferencias.getInstance();
			coordinacion.preferencias.Notificaciones notificaciones = (coordinacion.preferencias.Notificaciones) ficha
					.getNotificacionesField();
			if (notificaciones.getActivar()) {
				ArrayList<String> correos = new ArrayList<String>();
				for (String correo : notificaciones.getCorreoField().getAll()) {
					if (correo != null)
						correos.add(correo);
				}
				MailService.getInstance()
						.send(correos, "Notificación", message);
			}

		} catch (Exception e) {
			tarea.addLog("Error en la notificación por correo",
					"No se pudo enviar la notificación por correo electrónico.");
		}

	}

}
