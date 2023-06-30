package it.xtreamdev.gflbe.util;

import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.SpreadsheetMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.SpreadsheetML.WorksheetPart;
import org.xlsx4j.jaxb.Context;
import org.xlsx4j.sml.Cell;
import org.xlsx4j.sml.Row;
import org.xlsx4j.sml.SheetData;

import java.util.Optional;

public class ExcelUtils {

    public static SpreadsheetMLPackage createSpreadsheet() throws InvalidFormatException {
        return SpreadsheetMLPackage.createPackage();
    }

    public static SheetData addSheet(SpreadsheetMLPackage packageMlPackage, String name) throws Exception {
        int sizeToAssign = packageMlPackage.getWorkbookPart().getJaxbElement().getSheets().getSheet().size() + 1;
        WorksheetPart worksheetPart = packageMlPackage.createWorksheetPart(new PartName("/xl/worksheets/sheet" + sizeToAssign + ".xml"), name, sizeToAssign);
        return worksheetPart.getContents().getSheetData();
    }

    public static Row addRow(SheetData sheet) {
        Row row = Context.getsmlObjectFactory().createRow();
        sheet.getRow().add(row);
        return row;
    }

    public static Row addRow(SheetData sheet, Object... cells) {
        Row row = Context.getsmlObjectFactory().createRow();
        for (Object cellValue : cells) {
            Cell cell = Context.getsmlObjectFactory().createCell();
            cell.setV(String.valueOf(Optional.ofNullable(cellValue).orElse("")));
            row.getC().add(cell);
        }
        sheet.getRow().add(row);
        return row;
    }


}
