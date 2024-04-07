package uz.hiparts.hipartsuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.jvnet.hk2.annotations.Service;
import uz.hiparts.hipartsuz.dto.ProductCreateUpdateDto;
import uz.hiparts.hipartsuz.dto.ProductDto;
import uz.hiparts.hipartsuz.model.Category;
import uz.hiparts.hipartsuz.model.Product;
import uz.hiparts.hipartsuz.repository.ProductRepository;
import uz.hiparts.hipartsuz.service.ProductService;
import uz.hiparts.hipartsuz.util.Validations;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    @Override
    public ProductDto create(ProductCreateUpdateDto dto) {
        if (productRepository.findByNameAndActive(dto.getName(), true).isPresent())
            throw new RuntimeException("Product already exists");
        return new ProductDto(productRepository.save(Product.builder()
                .isActive(true)
                .name(dto.getName())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .build()));

    }

    @Override
    public ProductDto update(ProductCreateUpdateDto dto) {
        Product product = productRepository.findByNameAndActive(dto.getName(), true)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (productRepository.findByNameAndActive(dto.getName(), true).isPresent())
            throw new RuntimeException("Product already exists");
        return new ProductDto(productRepository.save(Product
                .builder()
                .category(Validations.requireNonNullElse(dto.getCategory(), product.getCategory()))
                .name(Validations.requireNonNullElse(dto.getName(), product.getName()))
                .price(Validations.requireNonNullElse(dto.getPrice(), product.getPrice()))
                .isActive(Validations.requireNonNullElse(dto.isActive(), product.isActive()))
                .description(Validations.requireNonNullElse(dto.getDescription(), product.getDescription()))
                .build()));
    }

    @Override
    public ProductDto getById(Long id) {
        return new ProductDto(productRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new RuntimeException("Product not found")));
    }

    @Override
    public ProductDto getByName(String name) {
        return new ProductDto(productRepository.findByNameAndActive(name, true)
                .orElseThrow(() -> new RuntimeException("Product not found")));
    }

    @Override
    public ProductDto getByCategory(Category category) {
        return new ProductDto(productRepository.findByCategoryAndActive(category, true)
                .orElseThrow(() -> new RuntimeException("Product not found")));
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findByIdAndActive(id, true)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        product.setActive(false);
        productRepository.save(product);
    }
}
