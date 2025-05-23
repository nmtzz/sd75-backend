package gadgetify.server.services;

import gadgetify.server.dtos.Response;
import gadgetify.server.entities.Category;
import gadgetify.server.exceptions.ResourceNotFoundException;
import gadgetify.server.repositories.CategoryRepository;
import gadgetify.server.repositories.CategoryTranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryTranslationRepository categoryTranslationRepository;

    public Response<Category> save(Category category) {
        var savedCategory = categoryRepository.save(category);
        var categoryTranslations = category.getCategoryTranslations();
        categoryTranslations.forEach(categoryTranslation -> categoryTranslation.setCategory(savedCategory));
        categoryTranslationRepository.saveAll(categoryTranslations);
        return Response.<Category>builder()
                .result(savedCategory.toBasicResponse())
                .build();
    }

    public Response<List<Category>> findAll() {
        var categories = categoryRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return Response.<List<Category>>builder()
                .result(categories.stream().map(Category::toBasicResponse).toList())
                .build();
    }

    public Response<Category> findById(Integer id, boolean client) {
        var category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        if (client) {
            category.setCategoryTranslations(null);
        }
        return Response.<Category>builder()
                .result(category)
                .build();
    }
    public Response<Category> softDelete(Integer id) {
        var category = categoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        category.setStatus(false);
        return Response.<Category>builder()
                .result(categoryRepository.save(category.toBasicResponse()))
                .build();
    }
}
