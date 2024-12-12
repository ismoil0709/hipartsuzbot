package uz.hiparts.hipartsuz.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uz.hiparts.hipartsuz.dto.ProductCreateUpdateDto;
import uz.hiparts.hipartsuz.dto.ProductDto;
import uz.hiparts.hipartsuz.exception.NotFoundException;
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
        return new ProductDto(productRepository.save(Product.builder()
                .isActive(true)
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .imgPath(dto.getImgPath())
                .category(dto.getCategory())
                .discount(dto.getDiscount())
                .imgId(dto.getImgId())
                .build()));

    }

    @Override
    public ProductDto update(ProductCreateUpdateDto dto) {
        Product product = productRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException("Product"));
        return new ProductDto(productRepository.save(Product
                .builder()
                .id(dto.getId())
                .category(Validations.requireNonNullElse(dto.getCategory(), product.getCategory()))
                .name(Validations.requireNonNullElse(dto.getName(), product.getName()))
                .price(Validations.requireNonNullElse(dto.getPrice(), product.getPrice()))
                .description(Validations.requireNonNullElse(dto.getDescription(), product.getDescription()))
                .imgPath(Validations.requireNonNullElse(dto.getImgPath(), product.getImgPath()))
                .imgId(Validations.requireNonNullElse(dto.getImgId(), product.getImgId()))
                .discount(Validations.requireNonNullElse(dto.getDiscount(), product.getDiscount()))
                .isActive(true)
                .build()));
    }

    @Override
    public ProductDto getById(Long id) {
        return new ProductDto(productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product")));
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product"));
        productRepository.deleteById(id);
    }

    @Override
    public List<ProductDto> getAll() {
        return productRepository.findAll()
                .stream()
                .map(ProductDto::new)
                .toList();
    }

}
