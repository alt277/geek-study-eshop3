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
import ru.geekbrains.persist.model.Product;
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
    public String adminProductsPage(Model model) {
        model.addAttribute("activePage", "Products");
        model.addAttribute("products", productService.findAll());
        return "products";
    }

    @GetMapping("/product/{id}/edit")
    public String adminEditProduct(Model model, @PathVariable("id") Long id) {
        model.addAttribute("edit", true);
        model.addAttribute("activePage", "Products");
        model.addAttribute("product", productService.findById(id).orElseThrow(NotFoundException::new));
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        return "product_form";
    }

    @DeleteMapping("/product/{id}/delete")
    public String adminDeleteProduct(Model model, @PathVariable("id") Long id) {
        model.addAttribute("activePage", "Products");
        productService.deleteById(id);
        return "redirect:/products";
    }
    @GetMapping("/product/delete/picture/{pictureId}")
    public String adminDeletePicture(Model model, Product product,
                                     @PathVariable("pictureId") Long pictureId) throws IOException {
        model.addAttribute("activePage", "Products");

        productService.findById(product.getId()).get().getPictures().
                remove(pictureRepository.findById(pictureId));

        String fileName = pictureRepository.findById(pictureId).get().getPictureData().getFileName();
        Files.deleteIfExists(Paths.get(storagePath + "/" + fileName));

        return "redirect:/product_form";
    }
    @PostMapping("/product/delete/picture/{pictureId}")
    public String adminDeletePicture2(Model model, ProductRepr productRepr,
                                     @PathVariable("pictureId") Long pictureId) throws IOException {
        model.addAttribute("activePage", "Products");

        System.out.println("BEFORE METOD"+
                "pictureId= "+pictureId+"\n"
                +"pictureRepository.findById(pictureId)= "+pictureRepository.findById(pictureId)+"\n"
                +"productService.findById(product.getId()).get()= "+ productService.findById(productRepr.getId()).get()+"\n"
                +" productService.findById(product.getId()).get().getPictures()= "+ productService.findById(productRepr.getId()).get().getPictures()+"\n"
                +"pictureRepository.findById(pictureId)= "+pictureRepository.findById(pictureId)+"\n"
        );
//        productService.findProductbyID (productRepr.getId() ).get().getPictures()
//                .remove(pictureRepository.findById(pictureId));


        String fileName = pictureRepository.findById(pictureId).get().getPictureData().getFileName();
        Files.deleteIfExists(Paths.get(storagePath + "/" + fileName));

        System.out.println("AFTER METOD"+
                "pictureId= "+pictureId+"\n"
                +"pictureRepository.findById(pictureId)= "+pictureRepository.findById(pictureId)+"\n"
                +"productService.findById(product.getId()).get()= "+ productService.findById(productRepr.getId()).get()+"\n"
                +" productService.findById(product.getId()).get().getPictures()= "+ productService.findById(productRepr.getId()).get().getPictures()+"\n"
                +"pictureRepository.findById(pictureId)= "+pictureRepository.findById(pictureId)+"\n"
        );

        return "redirect:/products";
    }


    @GetMapping("/product/create")
    public String adminCreateProduct(Model model) {
        model.addAttribute("create", true);
        model.addAttribute("activePage", "Products");
        model.addAttribute("product", new ProductRepr());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("brands", brandRepository.findAll());
        return "product_form";
    }

    @PostMapping("/product")
    public String adminUpsertProduct(Model model, RedirectAttributes redirectAttributes, ProductRepr product) {
        model.addAttribute("activePage", "Products");

        try {
            productService.save(product);
        } catch (Exception ex) {
            logger.error("Problem with creating or updating product", ex);
            redirectAttributes.addFlashAttribute("error", true);
            if (product.getId() == null) {
                return "redirect:/product/create";
            }
            return "redirect:/product/" + product.getId() + "/edit";
        }
        return "redirect:/products";
    }
}
