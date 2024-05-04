package uz.hiparts.hipartsuz;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import uz.hiparts.hipartsuz.model.Branch;
import uz.hiparts.hipartsuz.model.Category;
import uz.hiparts.hipartsuz.model.Product;
import uz.hiparts.hipartsuz.repository.BranchRepository;
import uz.hiparts.hipartsuz.repository.CategoryRepository;
import uz.hiparts.hipartsuz.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
public class HipartsuzApplication {
    public static void main(String[] args) {
        SpringApplication.run(HipartsuzApplication.class,args);
    }
    @Bean
    public CommandLineRunner commandLineRunner(
            CategoryRepository categoryRepository,
            ProductRepository productRepository, BranchRepository branchRepository){
        return args -> {
            List<Category> categories = new ArrayList<>();
            categories.add(Category.builder().name("New").build());
            categories.add(Category.builder().name("PIZZA").build());
            categories.add(Category.builder().name("NIMADUR").build());
            categoryRepository.saveAll(categories);
            List<Product> products = new ArrayList<>();
            products.add(
                    Product.builder()
                            .name("Dog")
                            .category(new Category(1L,"New"))
                            .price(10_000D)
                            .description("This is a dog")
                            .isActive(true)
                            .discount(1.2D)
                            .imgPath("https://picsum.photos/id/237/200/300")
                            .build()
            );
            products.add(
                    Product.builder()
                            .name("Cat")
                            .category(new Category(2L,"PIZZA"))
                            .price(20_000D)
                            .description("This is a cat")
                            .isActive(true)
                            .discount(0D)
                            .imgPath("https://picsum.photos/200/300?grayscale")
                            .build()
            );
            products.add(
                    Product.builder()
                            .name("QQQQ")
                            .category(new Category(3L,"NIMADUR"))
                            .price(10_000D)
                            .description("This is a qqq")
                            .isActive(true)
                            .discount(1.2D)
                            .imgPath("https://picsum.photos/seed/picsum/200/300")
                            .build()
            );
            products.add(
                    Product.builder()
                            .name("Dog")
                            .category(new Category(3L,"NIMADUR"))
                            .price(10_000D)
                            .description("This is a dog")
                            .isActive(true)
                            .discount(1.2D)
                            .imgPath("https://picsum.photos/id/237/200/300")
                            .build()
            );
            productRepository.saveAll(products);
            branchRepository.saveAll(
                    List.of(
                            Branch.builder()
                                    .name("Tashkent")
                                    .lat(13331D)
                                    .lon(34223D)
                                    .build(),
                            Branch.builder()
                                    .name("Bukhara")
                                    .lat(1232D)
                                    .lon(2122D)
                                    .build()
                    )
            );
        };
    }
}
