package it.xtreamdev.gflbe.util;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ExcelUtils {

    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private AtomicInteger indexRow = new AtomicInteger();
    private AtomicReference<XSSFRow> riga;


    public void creaFoglioConHeader(String nomeFoglio, String titolo, List<String> titoliColonne) {
        workbook = new XSSFWorkbook();
        riga = null;
        indexRow = new AtomicInteger(0);
        sheet = workbook.createSheet(nomeFoglio);
        XSSFRow row = sheet.createRow(indexRow.getAndIncrement());
        XSSFCellStyle style = getStyleWithBorder(workbook);
        XSSFCellStyle styleWithFontBold = boldFont(workbook);
        createCellAndSetValue(workbook, row, styleWithFontBold, 0, titolo);
//            HEADER SECTION
        AtomicInteger cellHIndex = new AtomicInteger();
        XSSFRow headerRow = sheet.createRow(indexRow.getAndIncrement());
        titoliColonne.forEach(h -> {
            createCellAndSetValue(workbook, headerRow, styleWithFontBold, cellHIndex.getAndIncrement(), h);
        });
    }

    public void nuovaRiga(AtomicInteger colonna) {
        if(riga == null) {
            riga = new AtomicReference<>();
        }
        riga.set(sheet.createRow(indexRow.getAndIncrement()));
        colonna.set(0);
    }

    public void popolaRiga(AtomicInteger colonna, Object value) {
        XSSFCellStyle style = getStyleWithBorder(workbook);
        createCellAndSetValue(workbook, riga.get(), style, colonna.getAndIncrement(), value);
    }

    private void createDataRow() {
        riga = new AtomicReference<>(sheet.createRow(indexRow.getAndIncrement()));
    }

    public AtomicInteger createCellDIndex(){
        return new AtomicInteger();
    }

    public byte[] exportExcel() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        autoSize(sheet, 4);
        workbook.write(baos);
        baos.flush();
        baos.close();
        return baos.toByteArray();
    }

    public void createCellAndSetValue(XSSFWorkbook workbook,XSSFRow row, XSSFCellStyle style, int colNum, Object value) {
        XSSFCell cell = row.createCell(colNum);

        if (style != null) {
            cell.setCellStyle(style);
        }
        XSSFCellStyle rightAlignStyle=getStyleWithBorder(workbook);
        rightAlignStyle.setAlignment(HorizontalAlignment.RIGHT);

        if (value instanceof Double) {
            cell.setCellValue((Double) value);
            cell.setCellStyle(rightAlignStyle);
        } else if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
            cell.setCellStyle(rightAlignStyle);
        } else if (value instanceof String) {
            cell.setCellStyle(isValidDate(String.valueOf(value)) ? rightAlignStyle : style);
            cell.setCellValue((String) value);
        }
    }

    public void autoSize(XSSFSheet sheet, int numCol){
        for(int i = 0; i < numCol; i++ )
            sheet.autoSizeColumn(i);
    }
    public XSSFCellStyle getStyleWithBorder(XSSFWorkbook workbook) {
        XSSFCellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        return style;
    }

    public XSSFCellStyle boldFont(XSSFWorkbook workbook){
        XSSFFont font = workbook.createFont();
        XSSFCellStyle style = getStyleWithBorder(workbook);
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private boolean isValidDate(String data) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            sdf.parse(data);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }


}
