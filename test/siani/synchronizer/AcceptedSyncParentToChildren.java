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
		new Synchronizer(new File(parentHome, "Coleccion.mml"), new File(childHome, "Coleccion.mml")).sync();
		assertEquals(readFile(new File(expectedHome, "Coleccion.mml")), readFile(new File(childHome, "__Coleccion.mml")));
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


