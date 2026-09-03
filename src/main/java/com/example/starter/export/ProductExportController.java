package com.example.starter.export;

import com.example.starter.domain.Product;
import com.example.starter.service.ProductService;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.awt.Color;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/products/export")
public class ProductExportController {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ProductService productService;

    public ProductExportController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/excel")
    public void exportExcel(@RequestParam(value = "q", required = false) String keyword,
                            HttpServletResponse response) throws IOException {
        List<Product> products = productService.search(keyword);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encode("products.xlsx") + "\"");

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Products");
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            String[] headers = {"ID", "Name", "Description", "Price", "Quantity", "Created At"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Product product : products) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(product.getId());
                row.createCell(1).setCellValue(product.getName());
                row.createCell(2).setCellValue(nullToEmpty(product.getDescription()));
                row.createCell(3).setCellValue(product.getPrice().doubleValue());
                row.createCell(4).setCellValue(product.getQuantity());
                row.createCell(5).setCellValue(product.getCreatedAt() == null ? "" : product.getCreatedAt().format(DATE_TIME));
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(response.getOutputStream());
        }
    }

    @GetMapping("/pdf")
    public void exportPdf(@RequestParam(value = "q", required = false) String keyword,
                          HttpServletResponse response) throws IOException, DocumentException {
        List<Product> products = productService.search(keyword);
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encode("products.pdf") + "\"");

        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
        Paragraph title = new Paragraph("Product List", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(16f);
        document.add(title);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1f, 2.5f, 3.5f, 1.5f, 1.2f, 2f});

        addPdfHeader(table, "ID");
        addPdfHeader(table, "Name");
        addPdfHeader(table, "Description");
        addPdfHeader(table, "Price");
        addPdfHeader(table, "Qty");
        addPdfHeader(table, "Created At");

        for (Product product : products) {
            table.addCell(String.valueOf(product.getId()));
            table.addCell(product.getName());
            table.addCell(nullToEmpty(product.getDescription()));
            table.addCell(product.getPrice().toPlainString());
            table.addCell(String.valueOf(product.getQuantity()));
            table.addCell(product.getCreatedAt() == null ? "" : product.getCreatedAt().format(DATE_TIME));
        }

        document.add(table);
        document.close();
    }

    @GetMapping("/word")
    public void exportWord(@RequestParam(value = "q", required = false) String keyword,
                           HttpServletResponse response) throws IOException {
        List<Product> products = productService.search(keyword);
        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + encode("products.docx") + "\"");

        try (XWPFDocument document = new XWPFDocument()) {
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = title.createRun();
            run.setBold(true);
            run.setFontSize(16);
            run.setText("Product List");

            XWPFTable table = document.createTable(products.size() + 1, 6);
            XWPFTableRow header = table.getRow(0);
            header.getCell(0).setText("ID");
            header.getCell(1).setText("Name");
            header.getCell(2).setText("Description");
            header.getCell(3).setText("Price");
            header.getCell(4).setText("Qty");
            header.getCell(5).setText("Created At");

            for (int i = 0; i < products.size(); i++) {
                Product product = products.get(i);
                XWPFTableRow row = table.getRow(i + 1);
                row.getCell(0).setText(String.valueOf(product.getId()));
                row.getCell(1).setText(product.getName());
                row.getCell(2).setText(nullToEmpty(product.getDescription()));
                row.getCell(3).setText(product.getPrice().toPlainString());
                row.getCell(4).setText(String.valueOf(product.getQuantity()));
                row.getCell(5).setText(product.getCreatedAt() == null ? "" : product.getCreatedAt().format(DATE_TIME));
            }

            document.write(response.getOutputStream());
        }
    }

    private void addPdfHeader(PdfPTable table, String text) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(230, 230, 230));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String encode(String filename) {
        return URLEncoder.encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
    }
}
