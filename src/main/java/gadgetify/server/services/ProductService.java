package gadgetify.server.services;

import gadgetify.server.dtos.Response;
import gadgetify.server.entities.Product;
import gadgetify.server.entities.ProductImage;
import gadgetify.server.entities.ProductTranslation;
import gadgetify.server.exceptions.ResourceNotFoundException;
import gadgetify.server.repositories.ProductImageRepository;
import gadgetify.server.repositories.ProductRepository;
import gadgetify.server.repositories.ProductTranslationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductTranslationRepository productTranslationRepository;
    private final ProductImageRepository productImageRepository;

    public Response<Product> save(Product product) {
        var savedProduct = productRepository.save(product);

        if (product.getProductTranslations() != null && !product.getProductTranslations().isEmpty()) {
            product.getProductTranslations().forEach(translation -> {
                translation.setProduct(savedProduct);
                productTranslationRepository.save(translation);
            });
        }

        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            product.getProductImages().forEach(image -> {
                image.setProduct(savedProduct);
                productImageRepository.save(image);
            });
        }

        return Response.<Product>builder()
                .result(savedProduct.toBasicResponse())
                .build();
    }

    public Response<Product> update(Product product) {
        var updatedProduct = productRepository.save(product);

        if (product.getProductTranslations() != null && !product.getProductTranslations().isEmpty()) {
            productTranslationRepository.deleteAllByProductId(updatedProduct.getId());

            List<ProductTranslation> translations = product.getProductTranslations().stream()
                .peek(translation -> translation.setProduct(updatedProduct))
                .collect(Collectors.toList());
            productTranslationRepository.saveAll(translations);
        } else {
            productTranslationRepository.deleteAllByProductId(updatedProduct.getId());
        }

        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            productImageRepository.deleteAllByProductId(updatedProduct.getId());

            List<ProductImage> images = product.getProductImages().stream()
                .peek(image -> image.setProduct(updatedProduct))
                .collect(Collectors.toList());
            productImageRepository.saveAll(images);
        } else {
            productImageRepository.deleteAllByProductId(updatedProduct.getId());
        }

        return Response.<Product>builder()
                .result(updatedProduct.toBasicResponse())
                .build();
    }

    public Response<List<Product>> findAll() {
        var products = productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return Response.<List<Product>>builder()
                .result(products.stream().map(Product::toBasicResponse).toList())
                .build();
    }

    public Response<List<Product>> findAllByIdIn(List<Integer> ids) {
        var products = productRepository.findAllByIdIn(ids);
        return Response.<List<Product>>builder()
                .result(products.stream().map(Product::toBasicResponse).toList())
                .build();
    }

    public Response<Product> findById(Integer id, boolean client) {
        var product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setRatings(null);
        product.setCategory(null);
        if (client) {
            product.setProductTranslations(null);
            product.setPromotionDetails(null);
            var relatedProducts = productRepository.findRelatedProducts(id, product.getCategoryId());
            relatedProducts.forEach(Product::toBasicResponse);
            product.setRelatedProducts(relatedProducts);
        }
        return Response.<Product>builder()
                .result(product)
                .build();
    }

    public Response<List<String>> findAllBrands() {
        var brands = productRepository.findAllUniqueBrands();
        return Response.<List<String>>builder()
                .result(brands)
                .build();
    }

    public Response<List<String>> findAllSpecs() {
        var specs = productRepository.findAllSpecs();
        return Response.<List<String>>builder()
                .result(specs)
                .build();
    }

    public Response<List<String>> findAllSpecsByLanguage(String language) {
        var specs = productRepository.findAllSpecsByLanguage(language);
        return Response.<List<String>>builder()
                .result(specs)
                .build();
    }

    public Response<Product> softDelete(Integer id) {
        var product = productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        product.setStatus(false);
        productRepository.save(product);
        return Response.<Product>builder()
                .result(product)
                .build();
    }
}
