package uz.hiparts.hipartsuz.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/v1/image")
public class ImageController {
    @GetMapping("/get/{fileName}")
    public ResponseEntity<?> loadImage(@PathVariable String fileName) {
        String directoryPath = "/home/user/product_photo";
        Path filePath = Path.of(directoryPath, fileName);

        try {
            if (!Files.exists(filePath)) {
                System.out.println("File does not exist: " + fileName);
                return ResponseEntity.notFound().build();
            }
            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null) {
                mimeType = "application/octet-stream";
            }

            InputStreamResource resource = new InputStreamResource(Files.newInputStream(filePath));
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(mimeType));

            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (IOException ex) {
            System.err.println("Error occurred while loading file: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while loading the file.");
        }
    }

}
