package gadgetify.server.services;

import gadgetify.server.dtos.Response;
import gadgetify.server.entities.Cart;
import gadgetify.server.exceptions.ResourceNotFoundException;
import gadgetify.server.repositories.CartRepository;
import gadgetify.server.repositories.ProductRepository;
import gadgetify.server.utils.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Response<Void> addToCart(Integer productId, Integer quantity) {
        if (quantity > 3) {
            throw new IllegalArgumentException("Quantity must be less than 3, contact admin to increase stock");
        }
        LocaleContextHolder.setLocale(Locale.forLanguageTag("vi"));
        var userId = SecurityUtil.getCurrentUserId();
        var checkExist = cartRepository.findCartByUser_IdAndProduct_Id(userId, productId);
        if (checkExist.isPresent()) {
            var cart = checkExist.get();
            var currentStock = cart.getProduct().getStock();
            if (cart.getQuantity() >= currentStock || cart.getQuantity() + quantity > currentStock) {
                throw new IllegalArgumentException("Not enough stock");
            }
            var quantityCheck = cart.getQuantity() + quantity;
            if (quantityCheck > 3) {
                throw new IllegalArgumentException("Product already in cart, max quantity is 3, contact admin to increase stock");
            }
        }
        var product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        if (product.getStock() == null || product.getStock() < quantity) {
            throw new IllegalArgumentException("Not enough stock");
        }
        cartRepository.addToCart(userId, productId, quantity);
        return Response.<Void>builder()
                .build();
    }

    @Transactional
    public Response<List<Cart>> findByUserId() {
        var userId = SecurityUtil.getCurrentUserId();
        var carts = cartRepository.findAllByUserId(userId);
        if (carts != null && !carts.isEmpty()) {
            carts = carts.stream()
                    .filter(cart -> cart.getProduct().getStatus() && cart.getProduct().getCategory().getStatus() && cart.getProduct().getStock() != null && cart.getProduct().getStock() > 0)
                    .toList();
            if (carts.isEmpty()) {
                cartRepository.deleteByUserId(userId);
                return Response.<List<Cart>>builder()
                        .result(null)
                        .build();
            }
            carts.forEach(cart -> {
                if (cart.getQuantity() > cart.getCurrentStock()) {
                    cart.setQuantity(cart.getCurrentStock());
                    cartRepository.save(cart);
                }
            });
        }
        return Response.<List<Cart>>builder()
                .result(carts)
                .build();
    }

    public Response<Cart> increaseQuantity(Integer cartId) {
        var cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        if (cart.getQuantity() >= cart.getCurrentStock()) {
            throw new IllegalArgumentException("Not enough stock");
        }
        if (cart.getQuantity() < 3) {
            cart.setQuantity(cart.getQuantity() + 1);
        } else {
            throw new IllegalArgumentException("Quantity must be less than 3, contact admin to increase stock");
        }
        var updatedCart = cartRepository.save(cart);
        return Response.<Cart>builder()
                .result(updatedCart)
                .build();
    }

    public Response<Cart> decreaseQuantity(Integer cartId) {
        var cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
        if (cart.getQuantity() > 1) {
            cart.setQuantity(cart.getQuantity() - 1);
        }
        var updatedCart = cartRepository.save(cart);
        return Response.<Cart>builder()
                .result(updatedCart)
                .build();
    }

    @Transactional
    public Response<Void> removeFromCart(Integer cartId) {
        cartRepository.deleteById(cartId);
        return Response.<Void>builder()
                .build();
    }

    @Transactional
    public Response<Void> clearCart() {
        var userId = SecurityUtil.getCurrentUserId();
        cartRepository.deleteByUserId(userId);
        return Response.<Void>builder()
                .build();
    }
}
