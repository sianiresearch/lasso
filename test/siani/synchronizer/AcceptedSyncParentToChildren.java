package siani.synchronizer;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class AcceptedSyncParentToChildren {

	private static File parentHome = new File("test.res/parent/");
	private static File childHome = new File("test.res/children/");
	private static File expectedHome = new File("test.res/expected/");

	@Test
	public void should_Sync_Parent1_In_Child1() throws Exception {
		new Synchronizer(new File(parentHome, "01.txt"), new File(childHome, "01.txt")).sync();
	}

	@Test
	public void should_Sync_Parent2_In_Child2() throws Exception {
		new Synchronizer(new File(parentHome, "02.txt"), new File(childHome, "02.txt")).sync();
		assertEquals(readFile(new File(expectedHome, "02.txt")), readFile(new File(childHome, "__02.txt")));
	}

	@Test
	public void should_Sync_Collection() throws Exception {
		final String FILE = "Coleccion.mml";
		assertSync(FILE);
	}

	@Test
	public void should_Sync_Certificacion() throws Exception {
		final String FILE = "Certificacion.mml";
		assertSync(FILE);
	}

	@Test
	public void should_Sync_Cori_Workmap() throws Exception {
		final String FILE = "cori.Workmap.mml";
		assertSync(FILE);
	}

	@Test
	public void should_Sync_DataStoreBuilderComponente() throws Exception {
		final String FILE = "DataStoreBuilderComponente.mml";
		assertSync(FILE);
	}

	@Test
	public void should_Sync_EscritorioInspector() throws Exception {
		final String FILE = "EscritorioInspector.mml";
		assertSync(FILE);
	}

	@Test
	public void should_Sync_Expediente() throws Exception {
		final String FILE = "Expediente.mml";
		assertSync(FILE);
	}


	@Test
	public void should_Sync_All() throws Exception {
		for (File f : parentHome.listFiles((dir, name) -> !name.startsWith("__"))) {
			new Synchronizer(new File(parentHome, f.getName()), new File(childHome, f.getName())).sync();
			try {
				System.out.print("Starting File " + f.getName() + ". ");
				assertEquals(readFile(new File(expectedHome, f.getName())), readFile(new File(childHome, "__" + f.getName())));
				System.out.println("File " + f.getName() + " was successfully updated");
			} catch (AssertionError e) {
				System.err.println("File " + f.getName() + " has differences.");
			}
		}
	}

	private void assertSync(String FILE) {
		new Synchronizer(new File(parentHome, FILE), new File(childHome, FILE)).sync();
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


