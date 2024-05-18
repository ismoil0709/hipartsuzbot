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
}
