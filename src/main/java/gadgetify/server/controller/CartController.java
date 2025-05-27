package gadgetify.server.controller;

import gadgetify.server.dtos.Response;
import gadgetify.server.entities.Cart;
import gadgetify.server.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class CartController {

    private final CartService cartService;

    @PostMapping
    public Response<Void> addToCart(@RequestParam Integer productId, @RequestParam Integer quantity) {
        return cartService.addToCart(productId, quantity);
    }

    @GetMapping
    public Response<List<Cart>> findByUserId() {
        return cartService.findByUserId();
    }

    @PatchMapping("/increase")
    public Response<Cart> increaseQuantity(@RequestParam Integer cartId) {
        return cartService.increaseQuantity(cartId);
    }

    @PatchMapping("/decrease")
    public Response<Cart> decreaseQuantity(@RequestParam Integer cartId) {
        return cartService.decreaseQuantity(cartId);
    }

    @DeleteMapping("/{cartId}")
    public Response<Void> removeFromCart(@PathVariable Integer cartId) {
        return cartService.removeFromCart(cartId);
    }

    @DeleteMapping("/clear")
    public Response<Void> clearCart() {
        return cartService.clearCart();
    }
}
