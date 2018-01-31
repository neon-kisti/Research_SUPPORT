package com.diquest.scopus.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {

	FileOutputStream fs = null;
	FileInputStream inputStream = null;
	SXSSFWorkbook workbook;
	SXSSFSheet sheet = null;

	CellStyle style = null;
	private CreationHelper helper;

	public static void main(String... args) throws IOException {
		ExcelUtil u = new ExcelUtil();
		u.readExcel(new File("f:\\Documents\\Private\\2015\\IBS\\1~6월 71명+\\test.xlsx"));
	}

	private void createHeaderStyle(XSSFWorkbook workbook) {
		style = workbook.createCellStyle();
		style.setWrapText(true);
		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
	}

	public Map<Integer, LinkedList<String>> readExcel(File readFile) throws IOException {
		Map<Integer, LinkedList<String>> eidMap = new HashMap<Integer, LinkedList<String>>();
		XSSFRow row;
		XSSFCell cell;
		int eidColumnIndex = 4;
		XSSFWorkbook workbook = null;
		try {
			inputStream = new FileInputStream(readFile);
			System.out.println("read File " + readFile.getName());
			workbook = new XSSFWorkbook(inputStream);
			// sheet수 취득
			int sheetCn = workbook.getNumberOfSheets();
			System.out.println("sheet수 : " + sheetCn);
			StringBuilder buf = new StringBuilder();
			for (int sheetIndex = 0; sheetIndex < sheetCn; sheetIndex++) {
				String sheetName = workbook.getSheetName(sheetIndex);
				if ("ASJC Code".equalsIgnoreCase(sheetName)) {
					break;
				}
				XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
				// 취득된 sheet에서 rows수 취득
				int rows = sheet.getPhysicalNumberOfRows();

				System.out.println(workbook.getSheetName(sheetIndex) + " sheet의 row수 : " + rows);

				// 취득된 row에서 취득대상 cell수 취득
				LinkedList<String> list = new LinkedList<String>();
				int idx = 0;
				for (int rowIndex = 2; rowIndex < rows; rowIndex++) {
					row = sheet.getRow(rowIndex); // row 가져오기
					if (row != null) {
						cell = row.getCell(eidColumnIndex);
						if (cell != null) {
							String value = null;
							switch (cell.getCellType()) {
							case XSSFCell.CELL_TYPE_FORMULA:
								value = cell.getCellFormula();
								break;
							case XSSFCell.CELL_TYPE_NUMERIC:
								value = "" + cell.getNumericCellValue();
								break;
							case XSSFCell.CELL_TYPE_STRING:
								value = "" + cell.getStringCellValue();
								break;
							case XSSFCell.CELL_TYPE_BLANK:
								value = "[null 아닌 공백]";
								break;
							case XSSFCell.CELL_TYPE_ERROR:
								value = "" + cell.getErrorCellValue();
								break;
							default:
							}
							if (value != null) {
								list.add(idx++, value.trim());
							}
						}
					}
					eidMap.put(sheetIndex, list);
				}
			}
			// System.out.println(eidMap);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
		return eidMap;
	}

	public void writeExcel(File writeFile, Map<Integer, LinkedList<String>> mapData) throws IOException {
		XSSFRow row;
		XSSFCell cell;
		int startCreateCellIndex = 7;
		XSSFWorkbook workbook = null;
		try {
			inputStream = new FileInputStream(writeFile);
			workbook = new XSSFWorkbook(inputStream);
			// sheet수 취득
			int sheetCn = workbook.getNumberOfSheets();
			createHeaderStyle(workbook);
			helper = workbook.getCreationHelper();
			for (int sheetIndex = 0; sheetIndex < sheetCn; sheetIndex++) {
				String sheetName = workbook.getSheetName(sheetIndex);
				if ("ASJC Code".equalsIgnoreCase(sheetName)) {
					break;
				}
				XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
				int rows = sheet.getPhysicalNumberOfRows();
				int idx = 0;
				row = sheet.getRow(1); // row 가져오기
				write(sheet, row, startCreateCellIndex, "논문 ASJC", true, false);
				write(sheet, row, startCreateCellIndex + 1, "eid", true, false);
				write(sheet, row, startCreateCellIndex + 2, "issn", true, false);
				write(sheet, row, startCreateCellIndex + 3, "corr-author", true, false);
				sheet.setColumnWidth(startCreateCellIndex + 4, 3500);
				write(sheet, row, startCreateCellIndex + 4, "TC (피인용 수)", true, false);
				sheet.setColumnWidth(startCreateCellIndex + 5, 4000);
				write(sheet, row, startCreateCellIndex + 5, "저널명(Source)", true, false);
				sheet.setColumnWidth(startCreateCellIndex + 6, 3500);
				write(sheet, row, startCreateCellIndex + 6, "저널 대표\nasjc 1%", true, true);
				sheet.setColumnWidth(startCreateCellIndex + 7, 4000);
				write(sheet, row, startCreateCellIndex + 7, "저널 대표\nasjc 대분야 1%", true, true);
				sheet.setColumnWidth(startCreateCellIndex + 8, 4000);
				write(sheet, row, startCreateCellIndex + 8, "저널 1% 여부", true, false);
				sheet.setColumnWidth(startCreateCellIndex + 9, 4000);
				write(sheet, row, startCreateCellIndex + 9, "HCP 대표\nasjc 1%", true, true);
				sheet.setColumnWidth(startCreateCellIndex + 10, 4000);
				write(sheet, row, startCreateCellIndex + 10, "HCP 기타\nasjc 1%", true, true);
				sheet.setColumnWidth(startCreateCellIndex + 11, 4000);
				write(sheet, row, startCreateCellIndex + 11, "HCP 대표\nhcp 1% 여부", true, true);
				sheet.setColumnWidth(startCreateCellIndex + 12, 4000);
				write(sheet, row, startCreateCellIndex + 12, "HCP 대표+기타\nhcp 1% 여부", true, true);
				LinkedList<String> datas = mapData.get(sheetIndex);
				for (int rowIndex = 2; rowIndex < rows; rowIndex++) {
					row = sheet.getRow(rowIndex); // row 가져오기
					if (row != null && (datas.size() > idx)) {
						String data = datas.get(idx++);
						// System.out.println(data);
						String[] ds = data.split("\t");
						int l = startCreateCellIndex + ds.length;
						int dsIndex = 0;
						for (int dataIndex = startCreateCellIndex; dataIndex < l; dataIndex++) {
							write(sheet, row, dataIndex, ds[dsIndex++], false, false);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}

		FileOutputStream outstream = null;
		try {
			outstream = new FileOutputStream(writeFile);
			workbook.write(outstream);
			outstream.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (outstream != null) {
				outstream.flush();
				outstream.close();
			}
		}
		System.out.println("write File " + writeFile.getName());
	}

	private void write(XSSFSheet sheet, Row row, int cellIdx, String contents, boolean header, boolean isWarp) {
		Cell cell = row.createCell(cellIdx);
		cell.setCellType(XSSFCell.CELL_TYPE_STRING);
		if (header) {
			ClientAnchor anchor = null;
			Comment comment = null;
			RichTextString str = null;
			Drawing drawing = sheet.createDrawingPatriarch();
			switch (cellIdx) {
			case 7:
				str = helper.createRichTextString("문헌의 ASJC 전체 분류 코드\n2017.05.08.");
				break;
			case 8:
				str = helper.createRichTextString("논문의 EID\n2017.05.08.");
				break;
			case 9:
				str = helper.createRichTextString("논문이 게재된 저널의 ISSN 번호\n2017.05.08.");
				break;
			case 10:
				str = helper.createRichTextString("교신저자 명칭\n2017.05.08.");
				break;
			case 11:
				str = helper.createRichTextString("논문의 총 피인용 수\n2017.05.08.");
				break;
			case 12:
				str = helper.createRichTextString("논문이 게재된 저널명\n2017.05.08.");
				break;
			case 13:
				str = helper.createRichTextString("논문이 게제된 TOP 1% 저널의 대표 ASJC 코드 정보\n2017.05.08.");
				break;
			case 14:
				str = helper.createRichTextString("논문이 게재된 TOP 1% 저널의 대표 ASJC 분류코드의 대분야가 매칭된 논문 ASJC 코드\n2017.05.08.");
				break;
			case 15:
				str = helper.createRichTextString("논문이 게재된 저널이 Impact Factor가 TOP 1%에 속하는지 여부\n2017.05.08.");
				break;
			case 16:
				str = helper.createRichTextString("HCP 1%에 포함되는 논문의 대표 ASJC 분류코드 정보\n2017.05.08.");
				break;
			case 17:
				str = helper.createRichTextString("HCP 1%에 포함되는 논문의 대표 ASJC 분류코드 정보를 제외한 나머지 ASJC 분류코드 정보\n2017.05.08.");
				break;
			case 18:
				str = helper.createRichTextString("논문의 대표 ASJC 분류코드가 HCP 1%에 속하는지 여부\n2017.05.08.");
				break;
			case 19:
				str = helper.createRichTextString("논문의 대표 + 기타 ASJC 분류코드가 HCP 1%에 속하는지 여부\n2017.05.08.");
				break;
			}
			if (str != null) {
				anchor = helper.createClientAnchor();
				anchor.setCol1(cellIdx);
				anchor.setCol2(cellIdx + 3);
				anchor.setRow1(row.getRowNum());
				anchor.setRow2(row.getRowNum() + 2);
				comment = drawing.createCellComment(anchor);
				comment.setString(str);
				cell.setCellComment(comment);
			}
			if (isWarp) {
				cell.setCellStyle(style);
			}
		}
		cell.setCellValue(contents);
	}

	public void flush() {
		try {
			workbook.write(fs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	public void close() {
		if (fs != null)
			try {
				flush();
				fs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

}
