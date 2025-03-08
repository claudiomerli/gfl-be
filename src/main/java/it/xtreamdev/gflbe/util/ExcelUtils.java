package it.xtreamdev.gflbe.util;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Optional;

public class ExcelUtils {

    public static XSSFWorkbook createSpreadsheet() {
        return new XSSFWorkbook();
    }

    public static XSSFSheet addSheet(XSSFWorkbook workbook, String name) {
        return workbook.createSheet(name);
    }

    public static void addEmptyRow(XSSFSheet sheet) {
        int rowNum = sheet.getLastRowNum() + 1;
        sheet.createRow(rowNum);
    }

    public static void addRow(XSSFSheet sheet, Object... cells) {
        int rowNum = sheet.getLastRowNum() + 1;
        XSSFRow row = sheet.createRow(rowNum);

        for (int i = 0; i < cells.length; i++) {
            XSSFCell cell = row.createCell(i);
            Object cellValue = Optional.ofNullable(cells[i]).orElse("");

            if (cellValue instanceof Number) {
                cell.setCellValue(((Number) cellValue).doubleValue());
            } else {
                cell.setCellValue(cellValue.toString());
            }
        }
    }
}
