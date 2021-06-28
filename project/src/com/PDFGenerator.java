package com;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;

public class PDFGenerator {
    static final String destination = Paths.get("files/report.pdf").toAbsolutePath().toString();
    private ApplicationFrame parentFrame;

    private static String  LOWER_QUARTILE = "lowerQuartile";
    private static String  UPPER_QUARTILE = "upperQuartile";

    public PDFGenerator (ApplicationFrame frame) {
        parentFrame = frame;
    }

    /**
     * Generates a pdf file that is supposed to be a report. WIP.
     */
    public void generateReport () {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(destination));
            document.open();
            writeAverages(document);
            writeQuartiles(document, PDFGenerator.UPPER_QUARTILE);
            writeQuartiles(document, PDFGenerator.LOWER_QUARTILE);
            addImage(document);
            document.close();
            Desktop.getDesktop().open(new File(destination));
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeAverages (Document document) throws DocumentException {
        document.add(new Paragraph("Average grades by module:"));
        PdfPTable table = new PdfPTable(parentFrame.averages.headers.length);
        table.setSpacingBefore(15);
        table.setSpacingAfter(15);
        Font headerFont = new Font(Font.getFamily("Helvetica"), 10, Font.BOLDITALIC);
        Font font = new Font(Font.getFamily("Helvetica"), 8, Font.NORMAL);
        for (String headerContents : parentFrame.averages.headers) {
            String printHeader = headerContents;
            if (printHeader.equals("0")) {
                printHeader = "";
            }
            PdfPCell cell = new PdfPCell(new Phrase(printHeader, headerFont));
            table.addCell(cell);
        }
        for (String[] row : parentFrame.averages.data) {
            for (String cellContents : row) {
                String printCell = cellContents;
                if (printCell.equals("0")) {
                    printCell = "";
                }
                PdfPCell cell = new PdfPCell(new Phrase(printCell, font));
                table.addCell(cell);
            }
        }
        document.add(table);
    }

    private void writeQuartiles(Document document, String quartile) throws DocumentException {
        String[] headers = new String[] {"Module code", "High performance module marks", "Low performance module marks"};
        String[][] data = (String[][]) parentFrame.tree.get("data", "data", "table", quartile);
        if (quartile.equals(PDFGenerator.UPPER_QUARTILE)) {
            document.add(new Paragraph("Modules above 70% and below 40% marks for top 25% of students:"));
        }
        else {
            document.add(new Paragraph("Modules above 70% and below 40% marks for bottom 25% of students:"));
        }
        PdfPTable table = new PdfPTable(headers.length);
        table.setSpacingBefore(15);
        table.setSpacingAfter(15);
        Font headerFont = new Font(Font.getFamily("Helvetica"), 10, Font.BOLDITALIC);
        Font font = new Font(Font.getFamily("Helvetica"), 8, Font.NORMAL);
        for (String headerContents : headers) {
            String printHeader = headerContents;
            if (printHeader.equals("0")) {
                printHeader = "";
            }
            PdfPCell cell = new PdfPCell(new Phrase(printHeader, headerFont));
            table.addCell(cell);
        }
        System.out.println(Arrays.deepToString(headers));
        System.out.println(Arrays.toString(data));
        for (String[] row : data) {
            for (String cellContents : row) {
                String printCell = cellContents;
                System.out.println(printCell);
                if (printCell == null || printCell.equals("0")) {
                    printCell = "";
                }
                PdfPCell cell = new PdfPCell(new Phrase(printCell, font));
                table.addCell(cell);
            }
        }
        document.add(table);
    }
    private void addImage (Document document) throws IOException {
        try {
            Image image = Image.getInstance("files/graphs/barGraph [CE101-4-FY, CE101-4-SP, CE141-4-AU, CE141-4-FY, CE142-4-AU, CE142-4-FY, CE151-4-AU, CE152-4-SP, CE153-4-AU, CE154-4-SP, CE155-4-SP, CE161-4-AU, CE162-4-SP, CE163-4-AU, CE164-4-SP].jpg");
            image.scaleToFit(document.getPageSize().getWidth()-100, document.getPageSize().getHeight());
            image.setAlignment(Element.ALIGN_LEFT);
            document.add(image);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }
}
