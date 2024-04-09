package uz.hiparts.hipartsuz.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import uz.hiparts.hipartsuz.dto.CategoryDto;
import uz.hiparts.hipartsuz.model.Category;
import uz.hiparts.hipartsuz.service.CategoryService;

@RequiredArgsConstructor
@RestController("/api/v1/category")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/save")
    public ResponseEntity<Category> save(@Valid @RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.save(categoryDto));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Category> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @GetMapping("/get/name/{name}")
    public ResponseEntity<Category> getByName(@PathVariable String name) {
        return ResponseEntity.ok(categoryService.getByName(name));
    }
}
