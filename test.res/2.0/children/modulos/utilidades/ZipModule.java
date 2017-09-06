package coordinacion.modulos.utilidades;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipModule {

	private String source;
	private String output;

	public ZipModule(String source, String output) {
		this.source = source;
		this.output = output;
	}

	public void zip() throws FileNotFoundException, IOException {
		if (source != null && !source.isEmpty()) {
			FileOutputStream fos = null;
			ZipOutputStream zos = null;
			try {
				fos = new FileOutputStream(output);
				zos = new ZipOutputStream(fos);
				File file = new File(source);
				add(zos, file);
			} finally {
				if (zos != null) zos.close();
			}
		}
	}

	private void add(ZipOutputStream zip, File file) throws IOException {
		String path = file.getAbsolutePath();

		if (file.isDirectory())
			addDirectory(path, file.listFiles(), zip);

		else if (file.isFile())
			addFile(path, zip);
	}

	private void addDirectory(String path, File[] list, ZipOutputStream zip) throws IOException {
		if (!path.equals(source)) {
			String name = getDirectoryEntryName(path);
			ZipEntry entry = new ZipEntry(name);
			zip.putNextEntry(entry);
		}

		for (File file : list)
			add(zip, file);
	}

	private void addFile(String path, ZipOutputStream zip) throws IOException {
		String name = getFileEntryName(path);
		ZipEntry entry = new ZipEntry(name);
		zip.putNextEntry(entry);

		FileInputStream in = null;
		try {
			in = new FileInputStream(path);
			int len;
			byte buffer[] = new byte[1024];
			while ((len = in.read(buffer)) > 0) {
				zip.write(buffer, 0, len);
			}
		} finally {
			if (in != null) in.close();
		}
		zip.closeEntry();
	}

	private String getDirectoryEntryName(String path) {
		return path.substring(source.length() + 1, path.length()) + File.separator;
	}

	private String getFileEntryName(String path) {
		if (path.equals(source))
			return getFileName(path);

		return path.substring(source.length() + 1, path.length());
	}

	private String getFileName(String path) {
		if (!path.contains(File.separator)) return path;
		String[] elements = path.split(File.separator);
		return elements[elements.length - 1];
	}
}