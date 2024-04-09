package uz.hiparts.hipartsuz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uz.hiparts.hipartsuz.dto.ProductCreateUpdateDto;
import uz.hiparts.hipartsuz.dto.ProductDto;
import uz.hiparts.hipartsuz.model.Category;
import uz.hiparts.hipartsuz.service.ProductService;

@RequiredArgsConstructor
@RestController("/api/v1/product")
public class ProductController {
    private final ProductService productService;

    @PostMapping("/save")
    public ResponseEntity<ProductDto> save(@Valid @RequestBody ProductCreateUpdateDto dto) {
        return ResponseEntity.ok(productService.create(dto));
    }

    @PatchMapping("/update")
    public ResponseEntity<ProductDto> update(@Valid @RequestBody ProductCreateUpdateDto dto) {
        return ResponseEntity.ok(productService.update(dto));
    }

    @DeleteMapping("/delete")
    public void delete(Long id) {
        productService.delete(id);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ProductDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @GetMapping("/get/name/{name}")
    public ResponseEntity<ProductDto> getByName(@PathVariable String name) {
        return ResponseEntity.ok(productService.getByName(name));
    }

    @GetMapping("/get/category")
    public ResponseEntity<ProductDto> getByCategory(@Valid @RequestBody Category category) {
        return ResponseEntity.ok(productService.getByCategory(category));
    }
}
