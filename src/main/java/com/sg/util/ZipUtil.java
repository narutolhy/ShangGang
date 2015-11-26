package com.sg.util;

/**
 * Created by qml_moon on 25/11/15.
 */
import java.io.File;
import java.io.IOException;
import de.idyl.winzipaes.AesZipFileEncrypter;
import de.idyl.winzipaes.impl.AESEncrypterBC;

public class ZipUtil {

	public static void zipFile(String srcPath, String destPath, String name, String passWord) throws IOException {
		File file = new File(srcPath);
		File zipFile = new File(destPath);
		AESEncrypterBC bc = new AESEncrypterBC();
		AesZipFileEncrypter azfe = new AesZipFileEncrypter(zipFile, bc);
		azfe.add(file, "/" + name + ".txt", passWord);
		azfe.close();
	}

}