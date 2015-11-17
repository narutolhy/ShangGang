package com.sg;

import com.sg.spring.dao.CustomerDAO;
import com.sg.spring.dao.HarborDAO;
import com.sg.sql.model.Harbor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qml_moon on 16/11/15.
 */
@RestController
public class HarborController {

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public int handleFileUpload(@RequestParam("name") String name,
								@RequestParam("file") MultipartFile file,
								@RequestParam(value = "override") boolean override){

		ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
		HarborDAO harborDAO = (HarborDAO) context.getBean("harborDAO");

		if (!file.isEmpty()) {
			try {

				BufferedReader in = new BufferedReader(new InputStreamReader(file.getInputStream()));
				String line;
				List<Harbor> data = new ArrayList<Harbor>();
				while ((line = in.readLine()) != null) {
					int index = 0;
					while (index < line.length() && (line.charAt(index) == ' ' || line.charAt(index) == '\t')) {
						index++;
					}
					line = line.substring(index);
					String[] splits = line.split("\\s+");
					data.add(new Harbor(Double.parseDouble(splits[0]),
										Double.parseDouble(splits[1]),
										Double.parseDouble(splits[2])));
				}
				in.close();
				return harborDAO.insert(data, name, override);
			} catch (Exception e) {
				return -1;
			}
		} else {
			return 0;
		}
	}

	@RequestMapping(value = "/download", method = RequestMethod.POST)
	public ResponseEntity<InputStreamResource> getFile(@RequestParam("date") String date) throws IOException{

		ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
		HarborDAO harborDAO = (HarborDAO) context.getBean("harborDAO");

		String tmpFile = System.getProperty("java.io.tmpdir") + "/" + date + ".txt";
		System.out.println(tmpFile);
		harborDAO.dump(tmpFile, date);

		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.setContentType(MediaType.parseMediaType("application/force-download"));
		respHeaders.setContentDispositionFormData("attachment", date + ".txt");

		InputStreamResource isr = new InputStreamResource(new FileInputStream(new File(tmpFile)));
		return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
	}

	@RequestMapping(value = "/getdate", method = RequestMethod.GET)
	public String[] getDate() {

		ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
		HarborDAO harborDAO = (HarborDAO) context.getBean("harborDAO");

		return harborDAO.getAllDate();
	}

}
