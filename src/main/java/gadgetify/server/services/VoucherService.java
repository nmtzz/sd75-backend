package gadgetify.server.services;

import gadgetify.server.dtos.Response;
import gadgetify.server.entities.Voucher;
import gadgetify.server.exceptions.BadRequestException;
import gadgetify.server.exceptions.ResourceNotFoundException;
import gadgetify.server.repositories.VoucherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoucherService {

    private final VoucherRepository voucherRepository;

    public Response<List<Voucher>> findAll() {
        var vouchers = voucherRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return Response.<List<Voucher>>builder()
                .result(vouchers)
                .build();
    }

    public Response<List<Voucher>> findAllActive() {
        var vouchers = voucherRepository.findAllActiveVouchers(Instant.now());
        return Response.<List<Voucher>>builder()
                .result(vouchers)
                .build();
    }

    public Response<List<Voucher>> findAllByStatus(Boolean status) {
        var vouchers = voucherRepository.findAllByStatusOrderByEndDateDesc(status);
        return Response.<List<Voucher>>builder()
                .result(vouchers)
                .build();
    }

    public Response<Voucher> findById(Integer id) {
        var voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));
        return Response.<Voucher>builder()
                .result(voucher)
                .build();
    }

    public Response<Voucher> findByCode(String code) {
        var voucher = voucherRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));
        return Response.<Voucher>builder()
                .result(voucher)
                .build();
    }

    @Transactional
    public Response<Voucher> save(Voucher voucher) {
        if (voucherRepository.existsByCode(voucher.getCode())) {
            throw new BadRequestException("Voucher code already exists");
        }

        if (voucher.getStartDate().isAfter(voucher.getEndDate())) {
            throw new BadRequestException("Start date must be before end date");
        }

        if (voucher.getUsed() == null) {
            voucher.setUsed(0);
        }

        if (voucher.getStatus() == null) {
            voucher.setStatus(true);
        }

        if (voucher.getDiscountType() == null ||
                (!voucher.getDiscountType().equals("PERCENTAGE") && !voucher.getDiscountType().equals("FIXED"))) {
            throw new BadRequestException("Invalid discount type");
        }

        if (voucher.getDiscountType().equals("PERCENTAGE") &&
                (voucher.getDiscountAmount().doubleValue() <= 0 || voucher.getDiscountAmount().doubleValue() > 100)) {
            throw new BadRequestException("Percentage discount must be between 0 and 100");
        }

        if (voucher.getMinOrderAmount() != null && voucher.getMaxOrderAmount() != null &&
                voucher.getMinOrderAmount().doubleValue() == 0 && voucher.getMaxOrderAmount().doubleValue() == 0) {
            voucher.setMinOrderAmount(null);
            voucher.setMaxOrderAmount(null);
        }

        if (voucher.getMaxDiscountAmount() != null && voucher.getMaxDiscountAmount().doubleValue() == 0) {
            voucher.setMaxDiscountAmount(null);
        }

        var savedVoucher = voucherRepository.save(voucher);
        return Response.<Voucher>builder()
                .result(savedVoucher)
                .build();
    }

    @Transactional
    public Response<Voucher> update(Voucher voucher) {
        var existingVoucher = voucherRepository.findById(voucher.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));

        if (!existingVoucher.getCode().equals(voucher.getCode()) &&
                voucherRepository.existsByCode(voucher.getCode())) {
            throw new BadRequestException("Voucher code already exists");
        }

        if (voucher.getStartDate().isAfter(voucher.getEndDate())) {
            throw new BadRequestException("Start date must be before end date");
        }

        if (voucher.getDiscountType() == null ||
                (!voucher.getDiscountType().equals("PERCENTAGE") && !voucher.getDiscountType().equals("FIXED"))) {
            throw new BadRequestException("Invalid discount type");
        }

        if (voucher.getDiscountType().equals("PERCENTAGE") &&
                (voucher.getDiscountAmount().doubleValue() <= 0 || voucher.getDiscountAmount().doubleValue() > 100)) {
            throw new BadRequestException("Percentage discount must be between 0 and 100");
        }

        if (voucher.getMinOrderAmount() != null && voucher.getMaxOrderAmount() != null &&
                voucher.getMinOrderAmount().doubleValue() == 0 && voucher.getMaxOrderAmount().doubleValue() == 0) {
            voucher.setMinOrderAmount(null);
            voucher.setMaxOrderAmount(null);
        }

        if (voucher.getMaxDiscountAmount() != null && voucher.getMaxDiscountAmount().doubleValue() == 0) {
            voucher.setMaxDiscountAmount(null);
        }

        var updatedVoucher = voucherRepository.save(voucher);
        return Response.<Voucher>builder()
                .result(updatedVoucher)
                .build();
    }

    @Transactional
    public Response<Voucher> deactivate(Integer id) {
        var voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));

        voucher.setStatus(false);
        var deactivatedVoucher = voucherRepository.save(voucher);

        return Response.<Voucher>builder()
                .result(deactivatedVoucher)
                .build();
    }

    @Transactional
    public void delete(Integer id) {
        var voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Voucher not found"));

        if (voucher.getOrders() != null && !voucher.getOrders().isEmpty()) {
            throw new BadRequestException("Cannot delete voucher that is associated with orders");
        }

        voucherRepository.delete(voucher);
    }
} 