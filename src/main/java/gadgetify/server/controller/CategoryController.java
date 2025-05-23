package gadgetify.server.controller;

import gadgetify.server.dtos.Response;
import gadgetify.server.entities.Category;
import gadgetify.server.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Response<Category> save(@Valid @RequestBody Category category) {
        return categoryService.save(category);
    }

    @GetMapping
    public Response<List<Category>> findAll() {
        return categoryService.findAll();
    }
    @GetMapping("/{id}")
    public Response<Category> findById(@PathVariable Integer id, @RequestParam(required = false, defaultValue = "false" ) boolean client) {
        return categoryService.findById(id, client);
    }

    @PutMapping
    public Response<Category> update(@Valid @RequestBody Category category) {
        return categoryService.save(category);
    }
    @PatchMapping("/soft-delete/{id}")
    public Response<Category> softDelete(@PathVariable Integer id) {
        return categoryService.softDelete(id);
    }
}
