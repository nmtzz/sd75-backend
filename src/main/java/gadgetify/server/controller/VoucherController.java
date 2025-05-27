package gadgetify.server.controller;

import gadgetify.server.dtos.Response;
import gadgetify.server.entities.Voucher;
import gadgetify.server.services.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping
    public Response<List<Voucher>> findAll() {
        return voucherService.findAll();
    }

    @GetMapping("/active")
    public Response<List<Voucher>> findAllActive() {
        return voucherService.findAllActive();
    }

    @GetMapping("/status/{status}")
    public Response<List<Voucher>> findAllByStatus(@PathVariable Boolean status) {
        return voucherService.findAllByStatus(status);
    }

    @GetMapping("/{id}")
    public Response<Voucher> findById(@PathVariable Integer id) {
        return voucherService.findById(id);
    }

    @GetMapping("/code/{code}")
    public Response<Voucher> findByCode(@PathVariable String code) {
        return voucherService.findByCode(code);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Response<Voucher> save(@Valid @RequestBody Voucher voucher) {
        return voucherService.save(voucher);
    }

    @PutMapping
    public Response<Voucher> update(@Valid @RequestBody Voucher voucher) {
        return voucherService.update(voucher);
    }

    @PatchMapping("/{id}/deactivate")
    public Response<Voucher> deactivate(@PathVariable Integer id) {
        return voucherService.deactivate(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        voucherService.delete(id);
    }
} 