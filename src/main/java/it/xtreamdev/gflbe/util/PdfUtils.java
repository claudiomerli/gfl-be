package it.xtreamdev.gflbe.util;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Component
public class PdfUtils {

    private ByteArrayOutputStream out;
    private PdfDocument pdfDoc;
    private Document doc;
    private Table table;

    private void init() {
        out = new ByteArrayOutputStream();
        pdfDoc = new PdfDocument(new PdfWriter(out));
        doc = new Document(pdfDoc, PageSize.A4.rotate());
    }

    public void exportPdf(String titolo, List<String> colonne) throws IOException {
        init();
        float[] array = new float[colonne.size()];
        Arrays.fill(array, 5f);
        table = new Table(UnitValue.createPercentArray(array));

        PdfFont f = PdfFontFactory.createFont(StandardFonts.HELVETICA);
        Cell cell = new Cell(1, colonne.size())
                .add(new Paragraph(titolo))
                .setFont(f)
                .setFontSize(10)
                .setFontColor(DeviceGray.WHITE)
                .setBackgroundColor(DeviceGray.BLACK)
                .setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell(cell);
        colonne.forEach(colonna -> table.addHeaderCell(new Cell().setBackgroundColor(new DeviceGray(0.75f)).add(new Paragraph(colonna))));
    }

    public void setValore(Object valore) {
        table.addCell(new Cell().setTextAlignment(TextAlignment.CENTER).add(new Paragraph(valore != null ? String.valueOf(valore) : "")));
    }

    public byte[] completaPDF() throws IOException {
        doc.add(table);
        doc.close();
        out.close();
        return out.toByteArray();
    }
}
