package gadgetify.server.controller;

import gadgetify.server.dtos.Response;
import gadgetify.server.dtos.Transaction;
import gadgetify.server.entities.Order;
import gadgetify.server.entities.Refund;
import gadgetify.server.services.InvoiceService;
import gadgetify.server.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;
    private final InvoiceService invoiceService;

    @Value("${sepay.apikey}")
    private String apiKey;

    @PostMapping
    public Response<Order> save(@RequestBody Order order) {
        String language = LocaleContextHolder.getLocale().getLanguage();
        return orderService.save(order, language);
    }

    @PostMapping("/pos")
    public Response<Order> saveForPos(@RequestBody Order order) {
        return orderService.saveForPos(order);
    }

    @GetMapping
    public Response<List<Order>> findAll() {
        return orderService.findAll();
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> updatePaymentStatus(@RequestBody Transaction transaction, @RequestHeader("Authorization") String authorization) {
        log.info("New transaction: {}", transaction);
        log.info("Authorization: {}", authorization);
        System.out.println("Is valid authorization: " + authorization.equals(apiKey));
        if (authorization.equals(apiKey)) {
            orderService.updatePaymentStatus(transaction);
        }
        String response = "{\"success\": true}";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public Response<Order> findById(@PathVariable Integer id, @RequestParam(required = false, defaultValue = "false") boolean checkPaymentStatus,
                                    @RequestParam(required = false, defaultValue = "false") boolean client) {
        return orderService.findDetailById(id, checkPaymentStatus, client);
    }

    @GetMapping("/user")
    public Response<List<Order>> findAllByUserId() {
        return orderService.findAllByUserId();
    }

    @PutMapping("/cancel/{id}")
    public Response<Void> cancelOrder(@PathVariable Integer id, @RequestParam(required = false, defaultValue = "") String reason) {
        return orderService.cancelOrder(id, reason);
    }

    @PostMapping("/cancel-and-refund/{id}")
    public Response<Void> cancelAndRefund(@PathVariable Integer id, @RequestBody Refund refund) {
        return orderService.cancelAndRefund(id, refund);
    }

    @GetMapping("/refund/{id}")
    public Response<Refund> findRefundByOrderId(@PathVariable Integer id) {
        return orderService.findRefundByOrderId(id);
    }

    @PatchMapping("/confirm-refund/{refundId}")
    public Response<Void> confirmRefund(@PathVariable Integer refundId) {
        return orderService.confirmRefund(refundId);
    }

    @PatchMapping("/update-status/{id}")
    public Response<Void> updateStatus(@PathVariable Integer id, @RequestParam String status) {
        return orderService.updateStatus(id, status);
    }

    @GetMapping("/invoice/{id}")
    public ResponseEntity<byte[]> downloadInvoice(@PathVariable Integer id) {
        try {
            byte[] pdfData = invoiceService.createInvoicePdf(id);

            if (pdfData == null) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice-" + id + ".pdf");
            headers.setContentLength(pdfData.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfData);

        } catch (Exception e) {
            log.error("Error generating invoice PDF for order {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
