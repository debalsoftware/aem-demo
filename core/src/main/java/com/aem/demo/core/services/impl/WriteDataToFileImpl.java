package com.aem.demo.core.services.impl;

import java.io.OutputStream;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;

import com.aem.demo.core.services.WriteDataToFile;

@Component(service = WriteDataToFile.class, immediate = true)
public class WriteDataToFileImpl implements WriteDataToFile {

	@Override
	public void addDataToFile(Map<Integer, String> resourceMap, SlingHttpServletResponse slingHttpServletResponse) {

		// Blank workbook
		XSSFWorkbook workbook = new XSSFWorkbook();

		// Create a blank sheet
		XSSFSheet sheet = workbook.createSheet("Data Report");

		Row createRow = sheet.createRow(0);

		Cell reportTitle = createRow.createCell(0);
		reportTitle.setCellValue("Page Path");

		int rowNumber = 0;

		Set<Integer> keySet = resourceMap.keySet();

		for (Integer key : keySet) {

			createRow = sheet.createRow(rowNumber++);
			String resourcePath = resourceMap.get(key);
			Cell resourcePathCell = createRow.createCell(0);
			resourcePathCell.setCellValue(resourcePath);

		}

		try {

			slingHttpServletResponse.setContentType("text/csv");
			slingHttpServletResponse.setHeader("Content-Disposition",
					"attachment; filename=" + "Page-Asset-Report" + ".csv");

			// Write the workbook in file system

			OutputStream out = slingHttpServletResponse.getOutputStream();
			workbook.write(out);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
