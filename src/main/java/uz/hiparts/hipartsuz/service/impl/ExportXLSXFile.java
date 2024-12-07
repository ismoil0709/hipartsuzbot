package uz.hiparts.hipartsuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.dto.ProductDto;
import uz.hiparts.hipartsuz.service.ProductService;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExportXLSXFile {
    private final ProductService productService;
    public XSSFWorkbook workbook = new XSSFWorkbook();
    public XSSFSheet sheet = workbook.createSheet("Products");
    private final Row headerRow = sheet.createRow(0);
    public String[] columns = {"ID", "Name", "Description", "Price", "Img path", "Img ID", "Category", "Is active", "Discount"};
    public String exportXLSXFile() throws IOException {
        List<ProductDto> products = productService.getAll();
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }
        for (int i = 0; i < products.size(); i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(products.get(i).getId());
            row.createCell(1).setCellValue(products.get(i).getName());
            row.createCell(2).setCellValue(products.get(i).getDescription());
            row.createCell(3).setCellValue(products.get(i).getPrice());
            row.createCell(4).setCellValue(products.get(i).getImgPath());
            row.createCell(5).setCellValue(products.get(i).getImgId());
            row.createCell(6).setCellValue(products.get(i).getCategory().getName());
            row.createCell(7).setCellValue(String.valueOf(products.get(i).isActive()));
            row.createCell(8).setCellValue(String.valueOf(products.get(i).getDiscount()));
        }
        Path UPLOAD_DIR = Path.of("/home/user/product_photo" + File.separator);
        FileOutputStream out;
        out = new FileOutputStream(UPLOAD_DIR + "products.xlsx");
        workbook.write(out);
        out.close();
        return UPLOAD_DIR + "products.xlsx";
    }
}
