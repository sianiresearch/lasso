package siani.synchronizer;

import org.junit.Test;

import java.io.File;

public class AcceptedSyncParentToChildren {

	private static File parent = new File("test.res/parent/");
	private static File child = new File("test.res/children/");

	@Test
	public void should_Sync_Parent1_In_Child1() throws Exception {
		new Synchronizer(new File(parent, "01.txt"), new File(child, "01.txt")).sync();
	}

	@Test
	public void should_Sync_Parent2_In_Child2() throws Exception {
		new Synchronizer(new File(parent, "02.txt"), new File(child, "02.txt")).sync();
	}

	@Test
	public void should_Sync_Collection() throws Exception {
		new Synchronizer(new File(parent, "Coleccion.mml"), new File(child, "Coleccion.mml")).sync();
	}
}
