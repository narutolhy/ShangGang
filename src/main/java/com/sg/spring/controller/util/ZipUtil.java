package com.sg.spring.controller.util;

/**
 * Created by qml_moon on 25/11/15.
 */
import java.io.File;
import java.io.IOException;
import de.idyl.winzipaes.AesZipFileEncrypter;
import de.idyl.winzipaes.impl.AESEncrypterBC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ZipUtil {

	public static void zipFile(String srcPath, String destPath, String name, String passWord) throws IOException {
		File file = new File(srcPath);
		File zipFile = new File(destPath);
		AESEncrypterBC bc = new AESEncrypterBC();
		AesZipFileEncrypter azfe = new AesZipFileEncrypter(zipFile, bc);
		azfe.add(file, name, passWord);
		azfe.close();
	}

	@Async
	public void deleteFile(String path) {
		try {
			Thread.sleep(60000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}

}