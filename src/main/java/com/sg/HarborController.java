package com.sg;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by qml_moon on 16/11/15.
 */
@RestController
public class HarborController {

	@RequestMapping(value="/upload", method= RequestMethod.POST)
	public int handleFileUpload(@RequestParam("name") String name,
							@RequestParam("file") MultipartFile file){
		if (!file.isEmpty()) {
			try {

				byte[] bytes = file.getBytes();
				BufferedOutputStream stream =
					new BufferedOutputStream(new FileOutputStream(new File(name)));
				stream.write(bytes);
				stream.close();
				return 1;
			} catch (Exception e) {
				return -1;
			}
		} else {
			return 0;
		}
	}

}
