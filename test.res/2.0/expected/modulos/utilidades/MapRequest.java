package coordinacion.modulos.utilidades;

import org.monet.bpi.HttpClient;
import org.monet.bpi.JSONArray;
import org.monet.bpi.JSONObject;
import org.monet.bpi.Setup;
import org.monet.bpi.types.Date;
import org.monet.bpi.types.Picture;
import org.monet.bpi.types.location.Point;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class MapRequest {

	HashMap<String, Objeto> puntosObj = new HashMap<String, Objeto>();

	public static class Objeto {
		Number latitud;
		Number longitud;
		String nombre;

		//    private Number reducir(Number num){
		private Number reducir(Number num) {
			String nr = String.valueOf(num);
//      if (nr.length() > 6){
//        nr = nr.substring(0,8);
			if (nr.length() > 6) {
				nr = nr.substring(0, 8);
			}
			Double previo = Double.valueOf(nr);
			Number numero = previo;
			return numero;

		}

		public Objeto(Number lat, Number lon, String nom) {

			latitud = reducir(lat);
			longitud = reducir(lon);
			nombre = nom;
		}

		public Number getLatitud() {
			return latitud;
		}

		public Number getLongitud() {
			return longitud;
		}

		public String getNombre() {
			return nombre;
		}

		public void setNombre(String nombre) {
			this.nombre = nombre;
		}

	}

	private String generateUrl(String url, String key) {

		UrlSigner signer;
		try {
			signer = new UrlSigner(key);
			String request = signer.signRequest(url);
			return request;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return "";
	}

	private ArrayList<String> getDevices(String user, String key) {
		StringBuilder url = new StringBuilder();
		url.append("http://lopesan.4gflota.com/acceso4/v12/GetDevices?user=");
		url.append(user);
		url.append("&format=json");
		String peticion = generateUrl(url.toString(), key);

		try {
			String respuesta = HttpClient.get(peticion);
			ArrayList<String> valores = new ArrayList<String>();
			Object borrar = JSONObject.parseFromUrl(peticion);

			JSONObject jsonObj = JSONObject.parseFromUrl(peticion);
			JSONArray result = jsonObj.getArray("Result");
			for (int i = 0; i < result.getCount(); i++) {
				JSONObject object = result.get(i);
				valores.add(String.valueOf(object.getInteger("DeviceID")));
			}

			return valores;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void getStopDevices(String user, String key, String idDevice,
								Date startDate, Date endDate) {
		StringBuilder url = new StringBuilder();

		url.append("http://lopesan.4gflota.com/acceso4/v12/GetDeviceStops?user=");
		url.append(user);
		url.append("&deviceID=");
		url.append(idDevice);
		url.append("&start=");
//    url.append(startDate.getValue().getTime()/1000);
//    //url.append("1363935600");
		url.append(startDate.getValue().getTime() / 1000);
		// url.append("1363935600");
		url.append("&end=");
//    url.append(endDate.getValue().getTime()/1000);
		url.append(endDate.getValue().getTime() / 1000);
		url.append("&format=json");

		String peticion = generateUrl(url.toString(), key);
		try {
			ArrayList<String> valores = new ArrayList<String>();

			JSONObject jsonObj = JSONObject.parseFromUrl(peticion);
			JSONArray result = jsonObj.getArray("Result");
			for (int i = 0; i < result.getCount(); i++) {

				JSONObject object = result.get(i);

				Objeto objeto = new Objeto(object.getNumber("Latitude"),
//            object.getNumber("Longitude"), object.getString("Address"));
						object.getNumber("Longitude"),
						object.getString("Address"));
				puntosObj.put(objeto.getNombre(), objeto);
			}

			// return valores;
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return null;
	}

	public Picture getMapa(String idDevice, Date startDate, Date endDate) {
		String urlmapa = null;
		try {
			if (idDevice != null) {
				//
				ArrayList<String> devices = new ArrayList<String>();
				String user = Setup.getVariable("_4gflotaUser");
				String apiKey = Setup.getVariable("_4gflotaApiKey");
				devices = getDevices(user, apiKey);

//        for (int i = 0; i < devices.size(); i++) {
//          getStopDevices(user, apiKey, devices.get(i), startDate, endDate);
//        }
//        for (int i = 0; i < devices.size(); i++) {
//          getStopDevices(user, apiKey, devices.get(i), startDate, endDate);
//        }

				getStopDevices(user, apiKey, idDevice, startDate, endDate);
				getStopDevices(user, apiKey, idDevice, startDate, endDate);

			}
			return Picture.fromUrl(generarMapaGoogle());
		} catch (Exception e) {
//      //urlmapa = "http://maps.googleapis.com/maps/api/staticmap?center=Las+Palmas,GC&zoom=13&size=500x500&sensor=true";      
//       // return Picture.fromUrl("http://maps.googleapis.com/maps/api/staticmap?&size=500x500&sensor=false");
			return null;
		}
	}

	//  public String generarMapaGoogle(){
	public String generarMapaGoogle() {
		String apiKey = "&key=" + Setup.getVariable("ApiKeyStaticMaps");
		String centro = "Las+Palmas,ES";
		StringBuilder parcialUrl = new StringBuilder();
		StringBuilder marcas = new StringBuilder();
//    parcialUrl.append("http://maps.googleapis.com/maps/api/staticmap?size=600x600&maptype=roadmap");
		parcialUrl
				.append("http://maps.googleapis.com/maps/api/staticmap?size=600x600&maptype=roadmap");
		marcas.append("&markers=");
//    for(Entry<String, Objeto> objeto: puntosObj.entrySet()){
		for (Entry<String, Objeto> objeto : puntosObj.entrySet()) {
			Objeto punto = objeto.getValue();
			String latitud = String.valueOf(punto.getLatitud());
			String longitud = String.valueOf(punto.getLongitud());
			marcas.append(latitud);
			marcas.append(",");
			marcas.append(longitud);
			marcas.append("%7C");
		}
//    //fin itero
		parcialUrl.append(marcas);
		parcialUrl.append("&sensor=false");
		String prefinalUrl = parcialUrl.toString();
//    //int ultimo = prefinalUrl.lastIndexOf("|");    
//    //String finalUrl = prefinalUrl.substring(0,ultimo-1) + prefinalUrl.substring(ultimo+1);
//    //return finalUrl;

		return parcialUrl.toString();
//    //String urlmapa = "http://maps.googleapis.com/maps/api/staticmap?size=512x512&maptype=roadmap&markers=size:mid%7Ccolor:red%7CPaseo+chil+15,GC%7Cpaseo+chil+25,GC%7Cpaseo+chil+5,GC&sensor=true";

	}

	//
//  
//  
//  public String generarMapaGoogleCoordenadas(String nombre, String latitud, String longitud){
	static public String generarMapaGoogleCoordenadas(String nombre, String latitud,
													  String longitud) {
		String apiKey = "&key=" + Setup.getVariable("ApiKeyStaticMaps");
		String centro = "Las+Palmas,ES";
		StringBuilder parcialUrl = new StringBuilder();
		StringBuilder marcas = new StringBuilder();
//	    parcialUrl.append("http://maps.googleapis.com/maps/api/staticmap?size=600x600&maptype=roadmap");
		parcialUrl
				.append("http://maps.googleapis.com/maps/api/staticmap?size=600x600&maptype=roadmap");
		marcas.append("&markers=");

		marcas.append(latitud);
		marcas.append(",");
		marcas.append(longitud);
		marcas.append("%7C");
		parcialUrl.append(marcas);
		parcialUrl.append("&sensor=false");
		String prefinalUrl = parcialUrl.toString();
//	    //int ultimo = prefinalUrl.lastIndexOf("|");    
//	    //String finalUrl = prefinalUrl.substring(0,ultimo-1) + prefinalUrl.substring(ultimo+1);
//	    //return finalUrl;

		return parcialUrl.toString();
	}
//  
//  
//  

	static public Picture getMapaCoordenadas(String nombre, Point localizacion) {

		try {
			String latitud = String.valueOf(localizacion.getX());
			String longitud = String.valueOf(localizacion.getY());
			String urlmapa = null;
//	      return Picture.fromUrl(generarMapaGoogleCoordenadas(nombre,latitud,longitud));
			return Picture.fromUrl(generarMapaGoogleCoordenadas(nombre,
					latitud, longitud));
		} catch (Exception e) {
//	      //urlmapa = "http://maps.googleapis.com/maps/api/staticmap?center=Las+Palmas,GC&zoom=13&size=500x500&sensor=true";      
//	        //return Picture.fromUrl("http://maps.googleapis.com/maps/api/staticmap?&size=500x500&sensor=false");
			return null;
		}
	}
//  

}
