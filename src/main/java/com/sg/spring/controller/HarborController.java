package com.sg.spring.controller;

import com.sg.spring.dao.HarborDAO;
import com.sg.sql.model.Harbor;
import com.sg.spring.controller.util.ZipUtil;
import com.sg.spring.controller.util.Prediction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by qml_moon on 16/11/15.
 */
@RestController
public class HarborController {

	@Autowired
	ZipUtil zipUtil;

	@Autowired
	Prediction prediction;

	HarborDAO harborDAO;

	public HarborController() {
		ApplicationContext context = new ClassPathXmlApplicationContext("Spring-Module.xml");
		harborDAO = (HarborDAO) context.getBean("harborDAO");
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public int handleFileUpload(@RequestParam("name") String name,
								@RequestParam("file") MultipartFile file,
								@RequestParam(value = "override") boolean override){

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
					data.add(new Harbor(Double.parseDouble(splits[1]),
										Double.parseDouble(splits[0]),
										Double.parseDouble(splits[2])));
				}
				in.close();
				try {
					String[] allDates = harborDAO.getAllDate();
					if (allDates[0].compareTo(name) <= 0) {
						prediction.updatePredictTable(data, name, harborDAO);
					}

					return harborDAO.insert(data, name, override);

				} catch (RuntimeException e) {
					return -1;
				}
			} catch (Exception e) {
				return -2;
			}
		} else {
			return -2;
		}
	}

	@RequestMapping(value = "/download", method = RequestMethod.POST)
	public ResponseEntity<InputStreamResource> getFile(@RequestParam("date") String date) throws IOException{

		String tmpFile = System.getProperty("java.io.tmpdir") + "/" + date + ".txt";
		String tmpZip = System.getProperty("java.io.tmpdir") + "/" + date + ".zip";

		System.out.println(tmpFile);
		List<Harbor> all = harborDAO.dump(date);
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
		for (Harbor harbor : all) {
			bw.write(harbor.toString());
		}
		bw.close();
		ZipUtil.zipFile(tmpFile, tmpZip, date, "123456");

		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.setContentType(MediaType.parseMediaType("application/force-download"));
		respHeaders.setContentDispositionFormData("attachment", date + ".zip");
		InputStreamResource isr = new InputStreamResource(new FileInputStream(new File(tmpZip)));
		deleteFile(tmpFile);
		zipUtil.deleteFile(tmpZip);
		return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
	}


	@RequestMapping(value = "/getdepthdata", method = RequestMethod.GET)
	public double[][] getData(@RequestParam("date") String date) throws IOException{

		List<Harbor> data = new ArrayList<Harbor>();
		String[] allDates = harborDAO.getAllDate();
		for (String d: allDates) {
			if (d.equals(date)) {
				data = harborDAO.dump(date);
			}
		}
		if (data.size() == 0 && allDates.length > 0 && date.compareTo(allDates[0]) > 0) {
			data = harborDAO.dump(allDates[0]);
			List<Harbor> trend = harborDAO.getPrevTrend();
			int numberOfMonth = (Integer.parseInt(date.substring(0, 2)) - Integer.parseInt(allDates[0].substring(0, 2))) * 12
				+ Integer.parseInt(date.substring(3, 5)) - Integer.parseInt(allDates[0].substring(3, 5));
			prediction.predict(data, trend, numberOfMonth);
		}

		double[][] result = new double[data.size()][3];
		for (int i = 0; i < data.size(); i++) {
			result[i][0] = data.get(i).getLatitude();
			result[i][1] = data.get(i).getLongitude();
			result[i][2] = data.get(i).getDepth();
		}

		return result;
	}

	@RequestMapping(value = "/getdate", method = RequestMethod.GET)
	public String[] getDate() {
		return harborDAO.getAllDate();
	}




	//----------------------------------------utility method-------------------------------------------



	private void deleteFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}

}
