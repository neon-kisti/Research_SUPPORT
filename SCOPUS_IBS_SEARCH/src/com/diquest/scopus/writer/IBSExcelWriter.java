package com.diquest.scopus.writer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class IBSExcelWriter {

	private String path;
	private File f;
	SXSSFWorkbook workbook = null;

	public IBSExcelWriter(File f) {
		this.f = f;
		init();
	}

	public void init() {
		workbook = new SXSSFWorkbook();
	}

	public void writeWook(List<Map<String, String>> result, String sheetName) {
		SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet(sheetName);
		Row r = sheet.createRow(0);
		Cell c = r.createCell(5);
		c.setCellValue("Author Role");
		c = r.createCell(7);
		c.setCellValue("Subject");

		r = sheet.createRow(1);
		c = r.createCell(0);
		c.setCellValue("Author");
		c = r.createCell(1);
		c.setCellValue("Title");
		c = r.createCell(2);
		c.setCellValue("Year");
		c = r.createCell(3);
		c.setCellValue("Document Type");
		c = r.createCell(4);
		c.setCellValue("EID");
		c = r.createCell(5);
		c.setCellValue("Co-Author");
		c = r.createCell(6);
		c.setCellValue("First-Correspondence author");
		c = r.createCell(7);
		c.setCellValue("논문 ASJC");
		c = r.createCell(8);
		c.setCellValue("eid");
		c = r.createCell(9);
		c.setCellValue("issn");

		int row = 2;
		for (int i = 0; i < result.size(); i++) {
			Map<String, String> data = result.get(i);
			Row dR = sheet.createRow(row);
			int cell = 0;
			Set<String> ks = data.keySet();
			for (String k : ks) {
				Cell dC = dR.createCell(cell);
				dC.setCellType(Cell.CELL_TYPE_STRING);
				dC.setCellValue(data.get(k));
				cell++;
			}
			row++;
		}
	}

	public void write() {
		OutputStream os = null;
		try {
			os = new FileOutputStream(f);
			workbook.write(os);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.flush();
				} catch (IOException e) {
				}
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}

	}
}
