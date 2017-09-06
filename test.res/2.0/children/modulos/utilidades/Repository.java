package coordinacion.modulos.utilidades;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Repository {

	private File directory;
	private File zipFile;
	private boolean isEmpty;

	public Repository(String path) {
		create(path);
		isEmpty = true;
	}

	public File getDirectory() {
		return this.directory;
	}

	public File getZipFile() {
		return this.zipFile;
	}

	public void create(String path) {
		(directory = new File(path)).mkdirs();
	}

	public void add(String path, org.monet.bpi.types.File file) throws FileNotFoundException, IOException {
		add(path, file.getContent());
	}

	public void add(String path, org.monet.bpi.types.Picture picture) throws FileNotFoundException, IOException {
		add(path, picture.getContent());
	}

	public void add(String path, String extension, org.monet.bpi.NodeDocument document) throws FileNotFoundException, IOException {
		add(path + extension, document.getContent());
	}

	public void add(String path, byte[] content) throws FileNotFoundException, IOException {
		File file = new File(directory, path);
		file.getParentFile().mkdirs();
		writeFile(file, content);
		isEmpty = false;
	}

	private void writeFile(File file, byte[] content) throws FileNotFoundException, IOException {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(content);
		} finally {
			if (fos != null) fos.close();
		}
	}

	public void zip(String path) throws FileNotFoundException, IOException {
		ZipModule zipModule = new ZipModule(directory.getAbsolutePath(), path);
		zipModule.zip();
		this.zipFile = new File(path);
	}

	public boolean delete() {
		return delete(directory) && delete(zipFile);
	}

	private static boolean delete(File file) {
		if (file == null || !file.exists()) return false;

		if (file.isDirectory())
			for (File childFile : file.listFiles())
				delete(childFile);

		return file.delete();
	}

	public boolean isEmpty() {
		return isEmpty;
	}
}