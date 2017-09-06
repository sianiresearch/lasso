package coordinacion.modulos.utilidades;

import coordinacion.Expedientes;
import coordinacion.expedientes.Index;
import coordinacion.modulos.componentes.coleccion.Componente;
import coordinacion.objetosactuacion.ObjetoActuacion;
import coordinacion.procesos.cori.expediente.FichaExpedienteCorrectivo;
import org.monet.bpi.*;
import org.monet.bpi.types.Date;
import org.monet.bpi.types.Link;
import org.monet.bpi.types.Term;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ObjectIterator {

	private static final String SLASH = File.separator;
	private static final String PDF_EXTENSION = ".pdf";
	private static final String UNKNOWN = "otro";

	private ObjetoActuacion objeto;
	private Repository repository;
	private ArrayList<String> treated;

	public ObjectIterator(ObjetoActuacion objeto, Repository repository) {
		this.objeto = objeto;
		this.repository = repository;
		treated = new ArrayList<String>();
	}

	public void fillRepository() throws FileNotFoundException, IOException {
		getExpedientDocumentsRoot("Expedientes" + SLASH);
//		getExpedientDocumentsWithChilds("Expedientes" + SLASH);
	}

	public void getExpedientDocumentsRoot(String path) throws FileNotFoundException, IOException {
		getCorrectives(path);
		getPreventives(path);
	}

	public void getCorrectives(String path) throws FileNotFoundException, IOException {
		Expedientes expedients = Expedientes.getInstance();
		Expression filter = filterObject(objeto);
		for (Index reference : expedients.get(filter)) {
			Node node = reference.getIndexedNode();
			getCorrectiveDocument(node, path);
		}
	}

	public void getPreventives(String path) throws FileNotFoundException, IOException {
		for (Node node : objeto.getLinksIn()) {
			getPreventive(node, path);
		}
		coordinacion.CatalogoObjetosActuacion catalogo = coordinacion.CatalogoObjetosActuacion.getInstance();
		for (coordinacion.catalogoobjetosactuacion.Index ref : catalogo.get(coordinacion.catalogoobjetosactuacion.Index.IdPadre.Eq(objeto.toLink().getId()))) {
			Node componente = ref.getIndexedNode();
			for (Node node : componente.getLinksIn())
				getPreventive(node, path);
		}
	}

	public void getPreventive(Node node, String path) throws FileNotFoundException, IOException {
		if (node != null && node instanceof coordinacion.procesos.copa.Expediente) {
			if (!treated.contains(node.toLink().getId())) {
				getPreventiveDocument(node, path);
				treated.add(node.toLink().getId());
			}
		}
	}

//	public void getExpedientDocumentsWithChilds(String path) throws FileNotFoundException, IOException {
//		ArrayList<Node> componentes = new ArrayList<Node>();
//		for (Node nodo: componentes){		
//			Expedientes expedients = Expedientes.getInstance();
//			Expression filter = filterObject(nodo);
//			for (Index reference : expedients.get(filter)){
//				Node node = reference.getIndexedNode();
//				getCorrectiveDocument(node, path);
//				getPreventiveDocument(node, path);
//			}
//		}
//	}	

	public void getCorrectiveDocument(Node node, String path) throws FileNotFoundException, IOException {

		if (node != null && node instanceof coordinacion.procesos.cori.Expediente) {
			coordinacion.procesos.cori.Expediente expediente = ((coordinacion.procesos.cori.Expediente) node);
			FichaExpedienteCorrectivo ficha = expediente.getFichaExpedienteCorrectivo();
			if (ficha.getEstado() != null && !ficha.getEstado().getKey().equals("cerrado")) {
				return;
			}

			String taskLabel = "";
			for (Task inverso : node.getLinkedTasks())
				taskLabel = inverso.getLabel();

			path = path + "Correctivos" + SLASH;
			String objeto = (ficha.getObjetoActuacion() != null) ? ficha.getObjetoActuacion().getLabel() : "SinObjeto";
			String pathObjeto = getFolderName(objeto) + SLASH;
			path = path + pathObjeto;

			coordinacion.procesos.cori.encargos.Coleccion encargos = ((coordinacion.procesos.cori.Expediente) node).getColeccion();
			for (coordinacion.modulos.encargos.coleccion.Index ref : encargos.getAll()) {
				getEncargosCorrectivos(ref.getIndexedNode(), path, taskLabel);
			}
		}
	}

	public void getEncargosCorrectivos(Node node, String path, String taskLabel) throws FileNotFoundException, IOException {
		if (node == null) return;
		if (node instanceof coordinacion.procesos.cori.encargos.EncargoCA) {
			coordinacion.procesos.cori.encargos.EncargoCA encargo = (coordinacion.procesos.cori.encargos.EncargoCA) node;
			getDocument(encargo.getFicha().getOt(), path, "OTAC - " + taskLabel);
			getDocument(encargo.getFicha().getPt(), path, "PTAC - " + taskLabel);
		}
		if (node instanceof coordinacion.procesos.cori.encargos.EncargoCI) {
			coordinacion.procesos.cori.encargos.EncargoCI encargo = (coordinacion.procesos.cori.encargos.EncargoCI) node;
			getDocument(encargo.getFicha().getOt(), path, "OTIC - " + taskLabel);
			getDocument(encargo.getFicha().getPt(), path, "PTIC - " + taskLabel);
		}
		if (node instanceof coordinacion.procesos.cori.encargos.EncargoCV) {
			coordinacion.procesos.cori.encargos.EncargoCV encargo = (coordinacion.procesos.cori.encargos.EncargoCV) node;
			getDocument(encargo.getFicha().getOt(), path, "OTV - " + taskLabel);
			getDocument(encargo.getFicha().getPt(), path, "PTV - " + taskLabel);
		}
	}

	public void getPreventiveDocument(Node node, String path) throws FileNotFoundException, IOException {
		String subPath = "Preventivos" + SLASH;
		String taskLabel = "";

		if (node != null && node instanceof coordinacion.procesos.copa.Expediente) {

			for (Task inverso : node.getLinkedTasks())
				taskLabel = inverso.getLabel();

			coordinacion.procesos.copa.FichaExpediente ficha = ((coordinacion.procesos.copa.Expediente) node).getFichaExpediente();
//			coordinacion.procesos.copa.fichaexpediente.SeccionControl seccionControl = (coordinacion.procesos.copa.fichaexpediente.SeccionControl) ficha.getSeccionControlField();
			//comprobar si cerrado
			boolean cerrado = false;
			for (org.monet.bpi.FieldComposite seccion : ficha.getSeccionRespuestaField().getAllFields()) {
				coordinacion.procesos.copa.fichaexpediente.SeccionRespuesta seccionRespuesta = (coordinacion.procesos.copa.fichaexpediente.SeccionRespuesta) seccion;
				if (seccionRespuesta.getNoValidado() == false) cerrado = true;
			}
			if (cerrado) {
				//			String filePath = path + subPath + getFolderName(seccionControl.getCodigo()) + SLASH;
				String filePath = path + subPath;
				Integer tamano = ficha.getPt().size();
				if (tamano > 0) {
					getDocument(ficha.getPt().get(tamano - 1), filePath, taskLabel);
				}
				//getDocument(ficha.getPt(), filePath,taskPath);
			}
		}
	}

	public void getDocument(List<Link> links, String path, String taskPath) throws FileNotFoundException, IOException {
		for (Link link : links)
			getDocument(link, path, taskPath);
	}

	public void getDocument(Link link, String path, String taskPath) throws FileNotFoundException, IOException {
		if (link != null && link.getId() != null && !link.getId().isEmpty()) {
			Node document = NodeService.get(link.getId());
			if (document != null && document instanceof NodeDocument) {
				NodeDocument documento = (NodeDocument) document;
//				repository.add(path + getDocumentLabel(documento), PDF_EXTENSION, documento);
				repository.add(path + getTaskLabel(taskPath), PDF_EXTENSION, documento);
			}
		}
	}

	private String getFolderName(Term term) {
		return (term != null) ? getFolderName(term.getLabel()) : UNKNOWN;
	}

	private String getFolderName(Date date) {
		return (date != null) ? getFolderName(date.toString("dd-MM-yyyy")) : UNKNOWN;
	}

	private String getFolderName(String name) {
		return (name != null && !name.isEmpty()) ? name.replace(SLASH, "-") : UNKNOWN;
	}

	private String getFileName(String name) {
		if (!name.contains(SLASH)) return name;
		String[] elements = name.split(SLASH);
		return elements[elements.length - 1];
	}

	private String getTaskLabel(String taskLabel) {
		return taskLabel.replaceAll("/", "-");
	}

	private String getDocumentLabel(NodeDocument document) {
		String label = document.getLabel();
		return label.replaceAll("/", "-");
	}

	private Expression filterObject(ObjetoActuacion objeto) {
		Expression filtro = Index.ObjetoActuacion.Eq(objeto.toLink());
		for (coordinacion.modulos.componentes.coleccion.Index referencia : objeto.getColeccionComponentes().getAll()) {
			if (referencia != null) {
				Node nodo = referencia.getIndexedNode();
				if (nodo != null && nodo instanceof Componente)
					filtro = Expression.Or(filtro, Index.ObjetoActuacion.Eq(nodo.toLink()));
			}
		}
		return filtro;
	}
}