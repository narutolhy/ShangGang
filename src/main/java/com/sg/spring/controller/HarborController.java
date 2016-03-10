package com.sg.spring.controller;

import com.sg.spring.controller.util.AES;
import com.sg.spring.controller.util.Coordinates;
import com.sg.spring.controller.util.ExcelAPI;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
								@RequestParam("harborId") int harborId,
								@RequestParam("dreged") boolean dreged,
								@RequestParam("override") boolean override){

		if (!file.isEmpty()) {
			try {

				BufferedReader in = new BufferedReader(new InputStreamReader(file.getInputStream()));
				String line;
				List<Harbor> data = new ArrayList<Harbor>();
				Set<String> singleton = new HashSet<String>();
				while ((line = in.readLine()) != null) {
					int index = 0;
					while (index < line.length() && (line.charAt(index) == ' ' || line.charAt(index) == '\t')) {
						index++;
					}
					line = line.substring(index);
					String[] splits = line.split("\\s+");
					if (splits.length < 3) {
						continue;
					}
					String e = splits[1] + splits[0];
					if (!singleton.contains(e)) {
						singleton.add(e);
						data.add(new Harbor(Double.parseDouble(splits[1]),
							Double.parseDouble(splits[0]),
							Double.parseDouble(splits[2])));
					}
				}
				in.close();
				if (data.get(0).getLatitude() < 100000) {
					throw new Exception("unsupported coordinate system.");
				}
				try {
					String[] allDates = harborDAO.getAllDate(harborId);
					if (allDates.length == 0 || allDates[0].compareTo(name) <= 0) {
						prediction.updatePredictTable(data, name, harborId, dreged, harborDAO);
					}

					return harborDAO.insert(data, name, harborId, override);

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

	@RequestMapping(value = "/encryptedupload", method = RequestMethod.POST)
	public int encryptedUpload(@RequestParam("data") String encryptedData,
							   @RequestParam("name") String name,
							   @RequestParam("harborId") int harborId,
							   @RequestParam("dreged") boolean dreged,
							   @RequestParam("override") boolean override) {
		try {
			AES aes = new AES("sjtucit");
			String output = aes.decrypt(encryptedData);
			String[] lines = output.split("\n");

			List<Harbor> data = new ArrayList<Harbor>();
			Set<String> singleton = new HashSet<String>();
			for (String line : lines) {
				int index = 0;
				while (index < line.length() && (line.charAt(index) == ' ' || line.charAt(index) == '\t')) {
					index++;
				}
				line = line.substring(index);
				String[] splits = line.split("\\s+");
				if (splits.length < 3) {
					continue;
				}
				String e = splits[1] + splits[0];
				if (!singleton.contains(e)) {
					singleton.add(e);
					data.add(new Harbor(Double.parseDouble(splits[1]),
						Double.parseDouble(splits[0]),
						Double.parseDouble(splits[2])));
				}
			}
			if (data.get(0).getLatitude() < 100000) {
				throw new Exception("unsupported coordinate system.");
			}
			try {
				String[] allDates = harborDAO.getAllDate(harborId);
				if (allDates.length == 0 || allDates[0].compareTo(name) <= 0) {
					prediction.updatePredictTable(data, name, harborId, dreged, harborDAO);
				}

				return harborDAO.insert(data, name, harborId, override);

			} catch (RuntimeException e) {
				return -1;
			}
		} catch (Exception e) {
			return -2;
		}
	}


	final static String[] exportType = new String[]{".xyz", ".xls"};

	@RequestMapping(value = "/download", method = RequestMethod.POST)
	public ResponseEntity<InputStreamResource> getFile(@RequestParam("date") String date,
													   @RequestParam("harborId") int harborId,
													   @RequestParam("userId") String userId,
													   @RequestParam("exportId") int exportId) throws IOException{

		String tmpFile = System.getProperty("java.io.tmpdir") + "/" + date + exportType[exportId];
		String tmpZip = System.getProperty("java.io.tmpdir") + "/" + date + ".zip";

//		System.out.println(tmpFile);
		List<Harbor> all = harborDAO.dump(date, harborId);
		if (exportId == 0) {
			BufferedWriter bw = new BufferedWriter(new FileWriter(tmpFile));
			for (Harbor harbor : all) {
				bw.write(harbor.toString());
			}
			bw.close();
		} else {
			ExcelAPI.exportToExcel(all, tmpFile);
		}

		ZipUtil.zipFile(tmpFile, tmpZip, "/" + date + exportType[exportId], userId);

		HttpHeaders respHeaders = new HttpHeaders();
		respHeaders.setContentType(MediaType.parseMediaType("application/force-download"));
		respHeaders.setContentDispositionFormData("attachment", date + ".zip");
		InputStreamResource isr = new InputStreamResource(new FileInputStream(new File(tmpZip)));
		deleteFile(tmpFile);
		zipUtil.deleteFile(tmpZip);
		return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
	}


	@RequestMapping(value = "/deleteharbor", method = RequestMethod.POST)
	public int delete(@RequestParam("date") String date,
					  @RequestParam("harborId") int harborId) throws IOException{

		return harborDAO.delete(date, harborId);
	}

	@RequestMapping(value = "/getdepthdata", method = RequestMethod.GET)
	public double[][] getData(@RequestParam("date") String date,
							  @RequestParam("harborId") int harborId) throws IOException{

		List<Harbor> data = new ArrayList<Harbor>();
		String[] allDates = harborDAO.getAllDate(harborId > 6 ? 6 : harborId);
		for (String d: allDates) {
			if (d.equals(date)) {
				data = selectData(harborDAO.dump(date, harborId > 6 ? 6 : harborId), harborId);
			}
		}

		if (data.size() == 0 && allDates.length > 0 && date.compareTo(allDates[0]) > 0) {
			data = selectData(harborDAO.dump(allDates[0], harborId > 6 ? 6 : harborId), harborId);
			List<Harbor> trend = selectData(harborDAO.getPrevTrend(harborId > 6 ? 6 : harborId), harborId);
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
	public String[] getDate(@RequestParam("harborId") int harborId) {
		return harborDAO.getAllDate(harborId);
	}


	@RequestMapping(value = "/backup", method = RequestMethod.POST)
	public int backup(@RequestParam("deviceId") int deviceId) {
		Runtime r = Runtime.getRuntime();
		try {
			Process p = r.exec("cmd /C mysqldump -u qmlmoon shanggang > D:\\backup\\backup.sql");
			if (p.waitFor() == 0) {
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	//----------------------------------------utility method-------------------------------------------



	private void deleteFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}

	private double convertWGSFormat(double o) {
		int degree = (int)Math.floor(o);
		int min = (int)Math.floor((o - degree) * 100);
		double second = (o - degree - 0.01 * min) * 10000;
		return degree + 1.0 * min / 60 + second / 3600;
	}

	static double[] division = new double[]{31.4, 31.367, 31.333, 31.3, 31.267, 31.233, 31.2, 31.167, 31.133, 31.1, 31.083};

	private List<Harbor> selectData(List<Harbor> origin, int harborId) {
		Coordinates.ToWGS(origin, Coordinates.meridian[(harborId > 6 ? 6 : harborId) - 1]);
		if (harborId < 6) {
			return origin;
		} else {
			List<Harbor> part = new ArrayList<Harbor>();
			harborId -= 6;
			for (Harbor h : origin) {
				if (h.getLatitude() > division[harborId] && h.getLatitude() < division[harborId - 1]) {
					part.add(h);
				}
			}

			return part;
		}
	}
}
