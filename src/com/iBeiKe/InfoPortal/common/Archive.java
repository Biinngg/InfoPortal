package com.iBeiKe.InfoPortal.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Archive {
	public void unzip(String source, String dest) throws IOException {
		ZipFile zipFile = new ZipFile(source);
		Enumeration<? extends ZipEntry> enu = zipFile.entries();
		while(enu.hasMoreElements()) {
			ZipEntry entry = enu.nextElement();
			InputStream stream = zipFile.getInputStream(entry);
			Files file = new Files(dest);
			file.saveStream(stream);
			stream.close();
		}
	}
}
