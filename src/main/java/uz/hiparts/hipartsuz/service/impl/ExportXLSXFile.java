package uz.hiparts.hipartsuz.service.impl;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import uz.hiparts.hipartsuz.dto.ProductDto;
import uz.hiparts.hipartsuz.service.ProductService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
public class ExportXLSXFile {

    private final ProductService productService;

    public ExportXLSXFile(ProductService productService) {
        this.productService = productService;
    }

    public String exportXLSXFile() throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Products");

        String[] columns = {"ID", "Name", "Description", "Price", "Img path", "Img ID", "Category", "Discount"};
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        List<ProductDto> products = productService.getAll();
        for (int i = 0; i < products.size(); i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(products.get(i).getId());
            row.createCell(1).setCellValue(products.get(i).getName());
            row.createCell(2).setCellValue(products.get(i).getDescription());
            row.createCell(3).setCellValue(products.get(i).getPrice());
            row.createCell(4).setCellValue(products.get(i).getImgPath());
            row.createCell(5).setCellValue(products.get(i).getImgId());
            row.createCell(6).setCellValue(products.get(i).getCategory().getName());
            row.createCell(7).setCellValue(String.valueOf(products.get(i).getDiscount()));
        }

        Path UPLOAD_DIR = Path.of(System.getProperty("user.home") + "/product_photo/files");
        File uploadDir = UPLOAD_DIR.toFile();
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        Path outputFilePath = UPLOAD_DIR.resolve("products.xlsx");
        try (FileOutputStream out = new FileOutputStream(outputFilePath.toFile())) {
            workbook.write(out);
        }

        workbook.close();
        return outputFilePath.toString();
    }
}
