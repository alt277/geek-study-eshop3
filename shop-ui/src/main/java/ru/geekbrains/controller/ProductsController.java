package ru.geekbrains.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.geekbrains.controller.repr.ProductRepr;
import ru.geekbrains.error.NotFoundException;
import ru.geekbrains.persist.repo.BrandRepository;
import ru.geekbrains.persist.repo.CategoryRepository;
import ru.geekbrains.persist.repo.PictureRepository;
import ru.geekbrains.service.ProductService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


@Controller
public class ProductsController {
    @Value("${picture.storage.path}")
    private String storagePath;
    private static final Logger logger = LoggerFactory.getLogger(ProductsController.class);

    private final ProductService productService;

    private final CategoryRepository categoryRepository;

    private final BrandRepository brandRepository;
   private final PictureRepository pictureRepository;

    @Autowired
    public ProductsController(ProductService productService, CategoryRepository categoryRepository,
                              BrandRepository brandRepository,PictureRepository pictureRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.pictureRepository =pictureRepository;
    }

    @GetMapping("/products")
    public String ProductsPage(Model model) {
        model.addAttribute("activePage", "Products");
        model.addAttribute("products", productService.findAll());
        return "products";
    }


    @GetMapping("/product_details")
    public String ProductsDetails(Model model) {

        model.addAttribute("activePage", "Products");
        model.addAttribute("productDetail", productService.findById(1l));
        return "product_details";
    }

}
