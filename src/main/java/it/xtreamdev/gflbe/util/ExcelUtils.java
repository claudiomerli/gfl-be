package it.xtreamdev.gflbe.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.List;
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

    public static List<Cell> addRow(XSSFSheet sheet, Object... cells) {
        int rowNum = sheet.getLastRowNum() + 1;
        XSSFRow row = sheet.createRow(rowNum);
        List<Cell> result = new ArrayList<>();

        for (int i = 0; i < cells.length; i++) {
            XSSFCell cell = row.createCell(i);
            Object cellValue = Optional.ofNullable(cells[i]).orElse("");

            if (cellValue instanceof Number) {
                cell.setCellValue(((Number) cellValue).doubleValue());
            } else {
                cell.setCellValue(cellValue.toString());
            }
            result.add(cell);
        }
        return result;
    }

    public static void addCellFormatStyle(Workbook wb, Cell cell, String format) {
        DataFormat fmt = wb.createDataFormat();
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setDataFormat(fmt.getFormat(format));

        cell.setCellStyle(cellStyle);
    }
}
