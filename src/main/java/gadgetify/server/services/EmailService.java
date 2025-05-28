package gadgetify.server.services;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import gadgetify.server.repositories.OrderRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    private final Configuration freemarkerConfig;
    private final OrderRepository orderRepository;

    @Value("${spring.mail.username}")
    private String senderEmail;
    private final static String COMPANY_NAME = "Gadgetify";

    @Async
    public CompletableFuture<Boolean> sendOrderConfirmedEmailAsync(Integer orderId, String language) {
        boolean success = sendOrderConfirmedEmail(orderId, language);
        return CompletableFuture.completedFuture(success);
    }

    public boolean sendOrderConfirmedEmail(Integer orderId, String language) {
        try {
            log.info("Sending order confirmation email for order {} in language {}", orderId, language);
            var order = orderRepository.findById(orderId)
                    .orElse(null);
            if (order == null) {
                log.error("Cannot send email for null order");
                return false;
            }

            if (order.getUser() == null || order.getUser().getEmail() == null) {
                log.error("Order {} has no associated user or email", order.getOrderCode());
                return false;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail, COMPANY_NAME);
            helper.setTo(order.getUser().getEmail());
            helper.setSubject("Order Confirmation: " + order.getOrderCode());

            String formattedDate = "";
            if (order.getCreatedAt() != null) {
                formattedDate = java.time.format.DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")
                        .withZone(java.time.ZoneId.systemDefault())
                        .format(order.getCreatedAt());
            }

            String paymentMethodDisplay = "Cash on Delivery";
            if (order.getPaymentMethod() != null) {
                paymentMethodDisplay = switch (order.getPaymentMethod()) {
                    case "CASH_ON_DELIVERY" -> getPaymentMethodByLanguage("CASH_ON_DELIVERY", language);
                    case "BANK_TRANSFER" -> getPaymentMethodByLanguage("BANK_TRANSFER", language);
                    default -> order.getPaymentMethod();
                };
            }

            Map<String, Object> model = new HashMap<>();
            model.put("order", order);
            model.put("formattedDate", formattedDate);
            model.put("paymentMethodDisplay", paymentMethodDisplay);

            String templateName = getTemplateNameByLanguage(language);
            Template template = freemarkerConfig.getTemplate(templateName);
            String htmlContent = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);

            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Order confirmation email sent to {} in language {}", order.getUser().getEmail(), language);
            return true;
        } catch (MessagingException | IOException | TemplateException e) {
            log.error("Failed to send order confirmation email", e);
            return false;
        }
    }

    @Async
    public CompletableFuture<Boolean> sendOrderCancellationEmailAsync(Integer orderId, String reason, String language) {
        boolean success = sendOrderCancellationEmail(orderId, reason, language);
        return CompletableFuture.completedFuture(success);
    }

    public boolean sendOrderCancellationEmail(Integer orderId, String reason, String language) {
        try {
            log.info("Sending order cancellation email for order {} in language {}", orderId, language);
            var order = orderRepository.findById(orderId)
                    .orElse(null);
            if (order == null) {
                log.error("Cannot send cancellation email for null order");
                return false;
            }

            if (order.getUser() == null || order.getUser().getEmail() == null) {
                log.error("Order {} has no associated user or email", order.getOrderCode());
                return false;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(senderEmail, COMPANY_NAME);
            helper.setTo(order.getUser().getEmail());

            String subject = "vi".equals(language) ?
                "Đơn hàng đã bị hủy: " + order.getOrderCode() :
                "Order Cancelled: " + order.getOrderCode();
            helper.setSubject(subject);

            String greeting = "vi".equals(language) ?
                "Xin chào " + (order.getUser().getFullName() != null ? order.getUser().getFullName() : "Khách hàng") :
                "Hello " + (order.getUser().getFullName() != null ? order.getUser().getFullName() : "Customer");

            String bodyTitle = "vi".equals(language) ?
                "Đơn hàng của bạn đã bị hủy" :
                "Your order has been cancelled";

            String orderCodeLabel = "vi".equals(language) ? "Mã đơn hàng:" : "Order Code:";
            String reasonText = "";
            if (reason != null && !reason.trim().isEmpty()) {
                String reasonLabel = "vi".equals(language) ? "Lý do hủy:" : "Cancellation Reason:";
                reasonText = "\n" + reasonLabel + " " + reason;
            }
            String thankYou = "vi".equals(language) ?
                "Cảm ơn bạn đã quan tâm đến sản phẩm của chúng tôi." :
                "Thank you for your interest in our products.";

            String textContent = String.format("%s,\n\n%s\n\n%s %s%s\n\n%s\n\nBest regards,\n%s Team",
                greeting,
                bodyTitle,
                orderCodeLabel,
                order.getOrderCode(),
                reasonText,
                thankYou,
                COMPANY_NAME
            );

            helper.setText(textContent, false);

            mailSender.send(message);
            log.info("Order cancellation email sent to {} in language {}", order.getUser().getEmail(), language);
            return true;
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send order cancellation email", e);
            return false;
        }
    }

    @Async
    public CompletableFuture<Boolean> sendOrderDeliveredEmailAsync(Integer orderId, String language) {
        boolean success = sendOrderDeliveredEmail(orderId, language);
        return CompletableFuture.completedFuture(success);
    }

    public boolean sendOrderDeliveredEmail(Integer orderId, String language) {
        try {
            log.info("Sending order delivered email for order {} in language {}", orderId, language);
            var order = orderRepository.findById(orderId)
                    .orElse(null);
            if (order == null) {
                log.error("Cannot send order delivered email for null order");
                return false;
            }

            if (order.getUser() == null || order.getUser().getEmail() == null) {
                log.error("Order {} has no associated user or email", order.getOrderCode());
                return false;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(senderEmail, COMPANY_NAME);
            helper.setTo(order.getUser().getEmail());

            String subject = "vi".equals(language) ?
                "Đơn hàng đã được giao thành công: " + order.getOrderCode() :
                "Order Delivered Successfully: " + order.getOrderCode();
            helper.setSubject(subject);

            String greeting = "vi".equals(language) ?
                "Xin chào " + (order.getUser().getFullName() != null ? order.getUser().getFullName() : "Khách hàng") :
                "Hello " + (order.getUser().getFullName() != null ? order.getUser().getFullName() : "Customer");

            String bodyTitle = "vi".equals(language) ?
                "Đơn hàng của bạn đã được giao thành công!" :
                "Your order has been delivered successfully!";

            String orderCodeLabel = "vi".equals(language) ? "Mã đơn hàng:" : "Order Code:";
            String deliveryInfo = "vi".equals(language) ?
                "Cảm ơn bạn đã mua sắm tại Gadgetify. Chúng tôi hy vọng bạn hài lòng với sản phẩm đã nhận được." :
                "Thank you for shopping with Gadgetify. We hope you are satisfied with the products you received.";

            String returnInfo = "vi".equals(language) ?
                "Nếu có bất kỳ vấn đề gì với đơn hàng, bạn có thể yêu cầu trả hàng trong vòng 7 ngày kể từ ngày giao hàng." :
                "If you have any issues with your order, you can request a return within 7 days from the delivery date.";

            String thankYou = "vi".equals(language) ?
                "Cảm ơn bạn đã tin tưởng và lựa chọn Gadgetify!" :
                "Thank you for trusting and choosing Gadgetify!";

            String textContent = String.format("%s,\n\n%s\n\n%s %s\n\n%s\n\n%s\n\n%s\n\nBest regards,\n%s Team",
                greeting,
                bodyTitle,
                orderCodeLabel,
                order.getOrderCode(),
                deliveryInfo,
                returnInfo,
                thankYou,
                COMPANY_NAME
            );

            helper.setText(textContent, false);

            mailSender.send(message);
            log.info("Order delivered email sent to {} in language {}", order.getUser().getEmail(), language);
            return true;
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send order delivered email", e);
            return false;
        }
    }

    @Async
    public CompletableFuture<Boolean> sendReturnAcceptedEmailAsync(Integer orderId, String language) {
        boolean success = sendReturnAcceptedEmail(orderId, language);
        return CompletableFuture.completedFuture(success);
    }

    public boolean sendReturnAcceptedEmail(Integer orderId, String language) {
        try {
            log.info("Sending return accepted email for order {} in language {}", orderId, language);
            var order = orderRepository.findById(orderId)
                    .orElse(null);
            if (order == null) {
                log.error("Cannot send return accepted email for null order");
                return false;
            }

            if (order.getUser() == null || order.getUser().getEmail() == null) {
                log.error("Order {} has no associated user or email", order.getOrderCode());
                return false;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(senderEmail, COMPANY_NAME);
            helper.setTo(order.getUser().getEmail());

            String subject = "vi".equals(language) ?
                "Yêu cầu trả hàng đã được chấp nhận: " + order.getOrderCode() :
                "Return Request Accepted: " + order.getOrderCode();
            helper.setSubject(subject);

            String greeting = "vi".equals(language) ?
                "Xin chào " + (order.getUser().getFullName() != null ? order.getUser().getFullName() : "Khách hàng") :
                "Hello " + (order.getUser().getFullName() != null ? order.getUser().getFullName() : "Customer");

            String bodyTitle = "vi".equals(language) ?
                "Yêu cầu trả hàng của bạn đã được chấp nhận" :
                "Your return request has been accepted";

            String orderCodeLabel = "vi".equals(language) ? "Mã đơn hàng:" : "Order Code:";
            String returnInfo = "vi".equals(language) ?
                "Chúng tôi sẽ liên hệ với bạn trong thời gian sớm nhất để xử lý việc trả hàng và hoàn tiền." :
                "We will contact you soon to process the return and refund.";

            String thankYou = "vi".equals(language) ?
                "Cảm ơn bạn đã tin tưởng và sử dụng dịch vụ của chúng tôi." :
                "Thank you for your trust and for using our service.";

            String textContent = String.format("%s,\n\n%s\n\n%s %s\n\n%s\n\n%s\n\nBest regards,\n%s Team",
                greeting,
                bodyTitle,
                orderCodeLabel,
                order.getOrderCode(),
                returnInfo,
                thankYou,
                COMPANY_NAME
            );

            helper.setText(textContent, false);

            mailSender.send(message);
            log.info("Return accepted email sent to {} in language {}", order.getUser().getEmail(), language);
            return true;
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send return accepted email", e);
            return false;
        }
    }

    @Async
    public CompletableFuture<Boolean> sendReturnRejectedEmailAsync(Integer orderId, String reason, String language) {
        boolean success = sendReturnRejectedEmail(orderId, reason, language);
        return CompletableFuture.completedFuture(success);
    }

    public boolean sendReturnRejectedEmail(Integer orderId, String reason, String language) {
        try {
            log.info("Sending return rejected email for order {} in language {}", orderId, language);
            var order = orderRepository.findById(orderId)
                    .orElse(null);
            if (order == null) {
                log.error("Cannot send return rejected email for null order");
                return false;
            }

            if (order.getUser() == null || order.getUser().getEmail() == null) {
                log.error("Order {} has no associated user or email", order.getOrderCode());
                return false;
            }

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(senderEmail, COMPANY_NAME);
            helper.setTo(order.getUser().getEmail());

            String subject = "vi".equals(language) ?
                "Yêu cầu trả hàng đã bị từ chối: " + order.getOrderCode() :
                "Return Request Rejected: " + order.getOrderCode();
            helper.setSubject(subject);

            String greeting = "vi".equals(language) ?
                "Xin chào " + (order.getUser().getFullName() != null ? order.getUser().getFullName() : "Khách hàng") :
                "Hello " + (order.getUser().getFullName() != null ? order.getUser().getFullName() : "Customer");

            String bodyTitle = "vi".equals(language) ?
                "Yêu cầu trả hàng của bạn đã bị từ chối" :
                "Your return request has been rejected";

            String orderCodeLabel = "vi".equals(language) ? "Mã đơn hàng:" : "Order Code:";
            String reasonText = "";
            if (reason != null && !reason.trim().isEmpty()) {
                String reasonLabel = "vi".equals(language) ? "Lý do từ chối:" : "Rejection Reason:";
                reasonText = "\n" + reasonLabel + " " + reason;
            }

            String contactInfo = "vi".equals(language) ?
                "Nếu bạn có bất kỳ thắc mắc nào, vui lòng liên hệ với chúng tôi để được hỗ trợ." :
                "If you have any questions, please contact us for assistance.";

            String thankYou = "vi".equals(language) ?
                "Cảm ơn bạn đã hiểu và thông cảm." :
                "Thank you for your understanding.";

            String textContent = String.format("%s,\n\n%s\n\n%s %s%s\n\n%s\n\n%s\n\nBest regards,\n%s Team",
                greeting,
                bodyTitle,
                orderCodeLabel,
                order.getOrderCode(),
                reasonText,
                contactInfo,
                thankYou,
                COMPANY_NAME
            );

            helper.setText(textContent, false);

            mailSender.send(message);
            log.info("Return rejected email sent to {} in language {}", order.getUser().getEmail(), language);
            return true;
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send return rejected email", e);
            return false;
        }
    }

    @Async
    public void sendForgetPasswordPinEmailAsync(String email, String pin, String fullName) {
        boolean success = sendForgetPasswordPinEmail(email, pin, fullName);
        CompletableFuture.completedFuture(success);
    }

    public boolean sendForgetPasswordPinEmail(String email, String pin, String fullName) {
        try {
            log.info("Sending forget password PIN email to {}", email);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(senderEmail, COMPANY_NAME);
            helper.setTo(email);
            helper.setSubject("Password Reset PIN - " + COMPANY_NAME);

            String greeting = "Hello " + (fullName != null ? fullName : "Customer");
            String bodyTitle = "Password Reset Request";
            String pinInfo = "Your password reset PIN is: " + pin;
            String expiryInfo = "This PIN will expire in 15 minutes for security reasons.";
            String instructions = "Please enter this PIN on the password reset page to continue with resetting your password.";
            String securityNote = "If you did not request a password reset, please ignore this email or contact us if you have concerns.";
            String thankYou = "Thank you for using our service.";

            String textContent = String.format("%s,\n\n%s\n\n%s\n\n%s\n\n%s\n\n%s\n\n%s\n\nBest regards,\n%s Team",
                greeting,
                bodyTitle,
                pinInfo,
                expiryInfo,
                instructions,
                securityNote,
                thankYou,
                COMPANY_NAME
            );

            helper.setText(textContent, false);

            mailSender.send(message);
            log.info("Forget password PIN email sent to {}", email);
            return true;
        } catch (MessagingException | UnsupportedEncodingException e) {
            log.error("Failed to send forget password PIN email", e);
            return false;
        }
    }

    private String getPaymentMethodByLanguage(String method, String language) {
        if ("vi".equals(language)) {
            return switch (method) {
                case "CASH_ON_DELIVERY" -> "Thanh toán khi nhận hàng";
                case "BANK_TRANSFER" -> "Chuyển khoản ngân hàng";
                default -> method;
            };
        } else {
            return switch (method) {
                case "CASH_ON_DELIVERY" -> "Cash on Delivery";
                case "BANK_TRANSFER" -> "Bank Transfer";
                default -> method;
            };
        }
    }

    private String getTemplateNameByLanguage(String language) {
        if ("en".equals(language)) {
            return "order-confirmed-en.ftl";
        }
        return "order-confirmed-vi.ftl";
    }
}
