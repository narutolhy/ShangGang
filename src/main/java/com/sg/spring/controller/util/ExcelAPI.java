package com.sg.spring.controller.util;

import com.sg.sql.model.Harbor;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.Number;
import jxl.write.WritableWorkbook;

import java.io.File;
import java.util.List;

/**
 * Created by qml_moon on 19/02/16.
 */
public class ExcelAPI {

	public static void exportToExcel(List<Harbor> data, String fileName) {
		try {
			WritableWorkbook book = Workbook.createWorkbook(new File(fileName));

			WritableSheet sheet = book.createSheet("第一页", 0);

			for (int i = 0; i < data.size(); i++) {
				Harbor curr = data.get(i);
				Number n1 = new Number(0, i, curr.getLatitude());
				Number n2 = new Number(1, i, curr.getLongitude());
				Number n3 = new Number(2, i, curr.getDepth());
				sheet.addCell(n1);
				sheet.addCell(n2);
				sheet.addCell(n3);
			}
			book.write();
			book.close();

		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
