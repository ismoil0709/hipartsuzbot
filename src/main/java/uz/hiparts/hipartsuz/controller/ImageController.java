package uz.hiparts.hipartsuz.controller;

import lombok.SneakyThrows;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/v1/image")
public class ImageController {
    @SneakyThrows
    @GetMapping("/get/{fileName}")
    public ResponseEntity<?> loadImage(@PathVariable String fileName) {
        Path path = Path.of(Paths.get("src", "main", "resources", "static", "product_photo").toAbsolutePath() + "/" + fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            InputStreamResource resource = new InputStreamResource(Files.newInputStream(path));
        return ResponseEntity.ok().headers(headers).body(resource);
    }
}
