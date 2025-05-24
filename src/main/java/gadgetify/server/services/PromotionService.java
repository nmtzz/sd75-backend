package gadgetify.server.services;

import gadgetify.server.dtos.Response;
import gadgetify.server.entities.Product;
import gadgetify.server.entities.Promotion;
import gadgetify.server.entities.PromotionDetail;
import gadgetify.server.exceptions.ResourceNotFoundException;
import gadgetify.server.repositories.PromotionDetailRepository;
import gadgetify.server.repositories.PromotionRepository;
import gadgetify.server.repositories.PromotionTranslationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionDetailRepository promotionDetailRepository;
    private final PromotionTranslationRepository promotionTranslationRepository;

    public Response<Promotion> save(Promotion promotion) {
        var savedPromotion = promotionRepository.save(promotion);
        var promotionDetails = promotion.getPromotionDetails();
        if (promotionDetails != null && !promotionDetails.isEmpty()) {
            promotionDetails.forEach(detail -> {
                detail.setPromotion(savedPromotion);
                var product = new Product();
                product.setId(detail.getProductId());
                detail.setProduct(product);
            });
            promotionDetailRepository.saveAll(promotionDetails);
        }
        var promotionTranslations = promotion.getPromotionTranslations();
        if (promotionTranslations != null && !promotionTranslations.isEmpty()) {
            promotionTranslations.forEach(translation -> translation.setPromotion(savedPromotion));
            promotionTranslationRepository.saveAll(promotionTranslations);
        }
        return Response.<Promotion>builder()
                .result(savedPromotion.toBasicResponse())
                .build();
    }

    @Transactional
    public Response<Promotion> update(Promotion promotion) {
        var updatedPromotion = promotionRepository.save(promotion);
        promotionDetailRepository.deleteAllByPromotionId(updatedPromotion.getId());
        var promotionDetails = promotion.getPromotionDetails();
        if (promotionDetails != null && !promotionDetails.isEmpty()) {
            promotionDetails.forEach(detail -> {
                detail.setPromotion(updatedPromotion);
                var product = new Product();
                product.setId(detail.getProductId());
                detail.setProduct(product);
            });
            promotionDetailRepository.saveAll(promotionDetails);
        }
        promotionTranslationRepository.deleteAllByPromotion_Id(updatedPromotion.getId());
        var promotionTranslations = promotion.getPromotionTranslations();
        if (promotionTranslations != null && !promotionTranslations.isEmpty()) {
            promotionTranslations.forEach(translation -> translation.setPromotion(updatedPromotion));
            promotionTranslationRepository.saveAll(promotionTranslations);
        }
        return Response.<Promotion>builder()
                .result(updatedPromotion.toBasicResponse())
                .build();
    }

    public Response<List<Promotion>> findAll() {
        var promotions = promotionRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return Response.<List<Promotion>>builder()
                .result(promotions.stream().map(Promotion::toBasicResponse).toList())
                .build();
    }

    public Response<Promotion> findById(Integer id) {
        var promotion = promotionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Promotion not found"));
        Set<Integer> productIds = promotion.getPromotionDetails().stream()
                .map(PromotionDetail::getProduct)
                .map(Product::getId)
                .collect(Collectors.toSet());
        promotion.setProductIds(productIds);
        return Response.<Promotion>builder()
                .result(promotion)
                .build();
    }

    @Transactional
    public Response<Void> delete(Integer id) {
        // Check if promotion exists
        var promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found"));
                
        promotionDetailRepository.deleteAllByPromotionId(id);
        
        promotionTranslationRepository.deleteAllByPromotion_Id(id);
        
        promotionRepository.delete(promotion);
        
        return Response.<Void>builder()
                .build();
    }
}
