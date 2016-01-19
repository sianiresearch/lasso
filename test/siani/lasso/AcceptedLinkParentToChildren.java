package siani.lasso;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class AcceptedLinkParentToChildren {

	private static File parentHome = new File("test.res/parent/");
	private static File childHome = new File("test.res/children/");
	private static File expectedHome = new File("test.res/expected/");

	@Test
	public void should_Link_Parent2_In_Child2() throws Exception {
		new Lasso(new File(parentHome, "02.txt"), new File(childHome, "02.txt"), false).execute();
		assertEquals(readFile(new File(expectedHome, "02.txt")), readFile(new File(childHome, "__02.txt")));
	}

	@Test
	public void should_Link_Collection() throws Exception {
		assertLink("Coleccion.mml");
	}

	@Test
	public void should_Link_Certificacion() throws Exception {
		assertLink("Certificacion.mml");
	}

	@Test
	public void should_Link_Cori_Workmap() throws Exception {
		assertLink("cori.Workmap.mml");
	}

	@Test
	public void should_Link_SlipIndents() throws Exception {
		assertLinkWithOutIndents("skipIndents.txt");
	}

	@Test
	public void should_Link_DataStoreBuilderComponente() throws Exception {
		assertLink("DataStoreBuilderComponente.mml");
	}

	@Test
	public void should_Link_EscritorioInspector() throws Exception {
		assertLink("EscritorioInspector.mml");
	}

	@Test
	public void should_Link_Expediente() throws Exception {
		assertLink("Expediente.mml");
	}

	@Test
	public void should_Link_Ficha() throws Exception {
		assertLink("Ficha.mml");
	}

	@Test
	public void should_Link_Ni() throws Exception {
		assertLink("Ni.mml");
	}

	@Test
	public void should_Link_ObjetoActuacion() throws Exception {
		assertLink("ObjetoActuacion.mml");
	}

	@Test
	public void should_Link_Otc() throws Exception {
		assertLink("Otc.mml");
	}

	@Test
	public void should_Link_Ptac() throws Exception {
		assertLink("Ptac.mml");
	}

	@Test
	public void should_Link_Ptap() throws Exception {
		assertLink("Ptap.mml");
	}

	@Test
	public void should_Link_Ptic() throws Exception {
		assertLink("Ptic.mml");
	}

	@Test
	public void should_Link_Ptv() throws Exception {
		assertLink("Ptv.mml");
	}

	@Test
	public void should_Link_Utils() throws Exception {
		assertLink("Utils.txt");
	}

	@Test
	public void should_Link_Sincronia() throws Exception {
		assertLink("Sincronia.mml");
	}


	@Test
	public void should_Link_Workmap() throws Exception {
		assertLink("Workmap.mml");
	}

	@Test
	public void should_Link_CatalogoObjetosActuacion() throws Exception {
		assertLink("CatalogoObjetosActuacion.mml");
	}

	@Test
	public void should_Link_EscritorioAdministradorSistema() throws Exception {
		assertLink("EscritorioAdministradorSistema.mml");
	}


	@Test
	public void should_Link_Expedientes() throws Exception {
		assertLink("Expedientes.mml");
	}

	private void assertLink(String FILE) {
		new Lasso(new File(parentHome, FILE), new File(childHome, FILE), false).execute();
		assertEquals("File " + FILE, readFile(new File(expectedHome, FILE)), readFile(new File(childHome, "__" + FILE)));
	}

	private void assertLinkWithOutIndents(String FILE) {
		new Lasso(new File(parentHome, FILE), new File(childHome, FILE), false, LassoComment.XML, true).execute();
		assertEquals("File " + FILE, readFile(new File(expectedHome, FILE)), readFile(new File(childHome, "__" + FILE)));
	}


	private String readFile(File file) {
		try {
			return new String(Files.readAllBytes(file.toPath()));
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
}


