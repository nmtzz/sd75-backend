package gadgetify.server.services;

import gadgetify.server.dtos.Response;
import gadgetify.server.dtos.Transaction;
import gadgetify.server.entities.*;
import gadgetify.server.exceptions.ResourceNotFoundException;
import gadgetify.server.repositories.*;
import gadgetify.server.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final VoucherRepository voucherRepository;
    private final RefundRepository refundRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;

    public Response<Order> save(Order order, String language) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("vi"));
        var userId = SecurityUtil.getCurrentUserId();
        order.setVoucherFromId();
        if (order.getVoucherId() != null) {
            var voucher = voucherRepository.findById(order.getVoucherId())
                    .orElseThrow(() -> new IllegalArgumentException("Voucher is not valid"));
            voucher.setUsed(voucher.getUsed() + 1);
            voucherRepository.save(voucher);
        }
        var user = User.builder().id(userId).build();
        order.setUser(user);
        order.setCreatedAt(Instant.now());
        order.setOrderCode("GAD" + order.getCreatedAt().getEpochSecond());
        var savedOrder = orderRepository.save(order);
        order.getOrderDetails().forEach(detail -> {
            detail.setOrder(savedOrder);
            var product = new Product();
            product.setId(detail.getProductId());
            detail.setProduct(product);
        });
        orderDetailRepository.saveAll(order.getOrderDetails());
        emailService.sendOrderConfirmedEmailAsync(savedOrder.getId(), language);
        return Response.<Order>builder()
                .result(savedOrder.toBasicResponse())
                .build();
    }
    public Response<Order> saveForPos(Order order) {
        order.setCreatedAt(Instant.now());
        order.setOrderCode("GAD" + order.getCreatedAt().getEpochSecond());
        var savedOrder = orderRepository.save(order);
        order.getOrderDetails().forEach(detail -> {
            detail.setOrder(savedOrder);
            var product = new Product();
            product.setId(detail.getProductId());
            detail.setProduct(product);
        });
        orderDetailRepository.saveAll(order.getOrderDetails());
        List<Integer> productIds =  order.getOrderDetails().stream()
                .map(OrderDetail::getProductId)
                .toList();
        List<Product> products = productRepository.findAllByIdIn(productIds);
        products.forEach(product -> {
            product.setSold(product.getSold() + 1);
            if (product.getStock() != null && product.getStock() > 0) product.setStock(product.getStock() - 1);
        });
        productRepository.saveAll(products);
        return Response.<Order>builder()
                .result(savedOrder.toBasicResponse())
                .build();
    }
    public Response<List<Order>> findAll() {
        var orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        orders.forEach(Order::toBasicResponse);
        return Response.<List<Order>>builder()
                .result(orders)
                .build();
    }

    public void updatePaymentStatus(Transaction transaction) {
        var order = orderRepository.findByOrderCode(transaction.getContent())
                .orElse(null);
        if (order != null && order.getStatus().equals("UNPAID") && order.getPaymentMethod().equals("BANK_TRANSFER")) {
            BigDecimal amount = transaction.getTransferAmount();
            if (amount.compareTo(order.getTotalAmount()) == 0) {
                order.setStatus("PENDING_CONFIRMATION");
                orderRepository.save(order);
            }
        }
    }
    public Response<Void> cancelOrder(Integer id) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (order.getStatus().equals("PENDING_CONFIRMATION") || order.getStatus().equals("UNPAID")) {
            order.setStatus("CANCELLED");
            var voucher = order.getVoucher();
            if (voucher != null) {
                voucher.setUsed(voucher.getUsed() - 1);
                voucherRepository.save(voucher);
            }
            orderRepository.save(order);
        }
        return Response.<Void>builder()
                .build();
    }
    public Response<Void> cancelAndRefund(Integer id, Refund refund) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (order.getStatus().equals("PENDING_CONFIRMATION") && order.getPaymentMethod().equals("BANK_TRANSFER")) {
            order.setStatus("CANCELLED");
            var voucher = order.getVoucher();
            if (voucher != null) {
                voucher.setUsed(voucher.getUsed() - 1);
                voucherRepository.save(voucher);
            }
            orderRepository.save(order);
            refund.setOrder(order);
            refund.setStatus("PENDING");
            refundRepository.save(refund);
        }
        return Response.<Void>builder()
                .build();
    }
    public Response<Void> updateStatus(Integer id, String status) {
        LocaleContextHolder.setLocale(Locale.forLanguageTag("vi"));
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setStatus(status);
        if (status.equals("SHIPPING")) {
            List<Product> products = order.getOrderDetails().stream()
                    .map(OrderDetail::getProduct)
                    .toList();
            products.forEach(product -> {
                product.setSold(product.getSold() + 1);
                if (product.getStock() != null && product.getStock() > 0) product.setStock(product.getStock() - 1);
            });
            productRepository.saveAll(products);
        }
        orderRepository.save(order);
        return Response.<Void>builder()
                .build();
    }

    public Response<Order> findDetailById(Integer id, boolean checkPaymentStatus, boolean client) {
        var order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (client) {
            order.getOrderDetails().forEach(OrderDetail::handleIsRated);
        }
        return Response.<Order>builder()
                .result(checkPaymentStatus ? order.toBasicResponse() : order)
                .build();
    }

    public Response<List<Order>> findAllByUserId() {
        var userId = SecurityUtil.getCurrentUserId();
        var orders = orderRepository.findAllByUser_Id(userId, Sort.by(Sort.Direction.DESC, "id"));
        orders.forEach(Order::fetchProductThumbnails);
        return Response.<List<Order>>builder()
                .result(orders.stream().map(Order::toBasicResponse).toList())
                .build();
    }

    public Response<Void> confirmRefund(Integer id) {
        var refund = refundRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Refund not found"));
        refund.setStatus("REFUNDED");
        refundRepository.save(refund);
        return Response.<Void>builder()
                .build();
    }

    public Response<Refund> findRefundByOrderId(Integer orderId) {
        var refund = refundRepository.findByOrder_Id(orderId)
                .orElse(null);
        if (refund == null) {
            return Response.<Refund>builder()
                    .result(null)
                    .build();
        }
        return Response.<Refund>builder()
                .result(refund)
                .build();
    }
}
