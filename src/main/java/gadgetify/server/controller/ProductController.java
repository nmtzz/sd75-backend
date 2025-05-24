package gadgetify.server.controller;

import gadgetify.server.dtos.Response;
import gadgetify.server.entities.Product;
import gadgetify.server.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response<Product> save(@RequestBody @Valid Product product) {
        return productService.save(product);
    }

    @PutMapping
    public Response<Product> update(@RequestBody @Valid Product product) {
        return productService.update(product);
    }

    @GetMapping
    public Response<List<Product>> findAll() {
        return productService.findAll();
    }


    @PostMapping("/ids")
    public Response<List<Product>> findAllByIdIn(@RequestBody List<Integer> ids) {
        return productService.findAllByIdIn(ids);
    }


    @GetMapping("/{id}")
    public Response<Product> findById(@PathVariable Integer id, @RequestParam(required = false, defaultValue = "false" ) boolean client) {
        return productService.findById(id, client);
    }

    @GetMapping("/brands")
    public Response<List<String>> findAllBrands() {
        return productService.findAllBrands();
    }

    @GetMapping("/specs")
    public Response<List<String>> findAllSpecs() {
        return productService.findAllSpecs();
    }

    @GetMapping("/specs/{language}")
    public Response<List<String>> findAllSpecsByLanguage(@PathVariable String language) {
        return productService.findAllSpecsByLanguage(language);
    }

    @PatchMapping("/soft-delete/{id}")
    public Response<Product> softDelete(@PathVariable Integer id) {
        return productService.softDelete(id);
    }
}
