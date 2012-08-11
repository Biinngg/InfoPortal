package com.iBeiKe.InfoPortal.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Archive {
	public void unzip(String source, String dest) throws IOException {
		ZipFile zipFile = new ZipFile(source);
		Enumeration enu = zipFile.entries();
		while(enu.hasMoreElements()) {
			ZipEntry entry = (ZipEntry)enu.nextElement();
			InputStream stream = zipFile.getInputStream(entry);
			Files file = new Files(dest);
			file.saveStream(stream);
			stream.close();
		}
	}
}
