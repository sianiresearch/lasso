package siani.lasso;

import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class AcceptedLinkParentToChildren2 {

	private static File parentHome = new File("test.res/2.0/parent/");
	private static File childHome = new File("test.res/2.0/children/");
	private static File expectedHome = new File("test.res/2.0/expected/");
	private static int correct = 0;
	private static int wrong = 0;

	@Test
	public void should_Link_Collection() throws Exception {
		for (File file : parentFiles())
			assertLink(file);
		System.out.println("correct: " + correct);
		System.out.println("wrong: " + wrong);
	}

	private List<File> parentFiles() {
		List<File> files = new ArrayList<>();
		list(parentHome.getAbsolutePath(), files);
		return files;
	}

	public void list(String directoryName, List<File> files) {
		File directory = new File(directoryName);
		File[] fList = directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(".mml") || file.isDirectory();
			}
		});
		if (fList == null) return;
		for (File file : fList) {
			if (file.isFile()) {
				files.add(file);
			} else if (file.isDirectory()) {
				list(file.getAbsolutePath(), files);
			}
		}
	}

	private void assertLink(File parentFile) {
		String aPackage = parentFile.getParent().replace(parentHome.getAbsolutePath(), "");
		if (!aPackage.isEmpty() && !aPackage.endsWith("/")) aPackage += "/";
		new Lasso(parentFile, new File(childHome, aPackage + parentFile.getName()), false).execute();
		final String expected = readFile(new File(expectedHome, aPackage + parentFile.getName()));
		final String actual = readFile(new File(childHome, aPackage + "__" + parentFile.getName()));
		if (expected.contentEquals(actual)) correct++;
		else {
			wrong++;
			System.out.println(parentFile.getPath());
		}
//		assertEquals("File " + file, expected, actual);
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


