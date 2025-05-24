package gadgetify.server.controller;

import gadgetify.server.dtos.Response;
import gadgetify.server.entities.Promotion;
import gadgetify.server.services.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;


    @PostMapping
    public Response<Promotion> save(@RequestBody Promotion promotion) {
        return promotionService.save(promotion);
    }

    @PutMapping
    public Response<Promotion> update(@RequestBody Promotion promotion) {
        return promotionService.update(promotion);
    }

    @GetMapping
    public Response<List<Promotion>> findAll() {
        return promotionService.findAll();
    }

    @GetMapping("/{id}")
    public Response<Promotion> findById(@PathVariable Integer id) {
        return promotionService.findById(id);
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable Integer id) {
        return promotionService.delete(id);
    }

}
