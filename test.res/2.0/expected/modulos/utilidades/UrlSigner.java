package coordinacion.modulos.utilidades;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class UrlSigner {
	private byte[] key; // Esta variable almacena la clave en binario para poder ejecutar el algoritmo de Base64.

	public UrlSigner(String keyString) throws IOException { //Decodifica una clave 'web safe' en Base64
		keyString = keyString.replace('-', '+');
		keyString = keyString.replace('_', '/');
		this.key = Base64.decode(keyString);
	}

	public String signRequest(String url) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, URISyntaxException {
		String resource = url;

		SecretKeySpec sha1Key = new SecretKeySpec(key, "HmacSHA1");    //Codifica la clave (previamente decodificada con el m�todo UrlSigner) en HMAC-SHA1.

		Mac mac = Mac.getInstance("HmacSHA1");     //Obtiene la instancia HMAC-SHA1 y la inicializa con la clave HMAC-SHA1.
		mac.init(sha1Key);

		byte[] sigBytes = mac.doFinal(resource.getBytes());  //Genera la firma binaria de la petici�n.

		String signature = Base64.encodeBytes(sigBytes);     //Codifica el resultado anterior a Base 64

		signature = signature.replace('+', '-');     //Adapta la firma a formato URL seguro
		signature = signature.replace('/', '_');

		return url + "&key=" + signature;    //Devuelve el resultado
	}
}
