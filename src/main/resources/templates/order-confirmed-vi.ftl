<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Xác Nhận Đơn Hàng - Gadgetify</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 600px;
            margin: 0 auto;
        }
        .header {
            text-align: center;
            padding: 20px 0;
            background-color: #f8f9fa;
        }
        .logo {
            font-size: 24px;
            font-weight: bold;
            color: #4a4a4a;
        }
        .content {
            padding: 20px;
        }
        .order-details {
            margin: 20px 0;
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 15px;
        }
        .order-item {
            margin-bottom: 10px;
            padding-bottom: 10px;
            border-bottom: 1px solid #eee;
        }
        .footer {
            text-align: center;
            font-size: 12px;
            color: #777;
            padding: 20px 0;
        }
        .total {
            font-weight: bold;
            margin-top: 15px;
        }
    </style>
</head>
<body>
    <div class="header">
        <div class="logo">Gadgetify</div>
    </div>
    <div class="content">
        <h2>Xác Nhận Đơn Hàng</h2>
        <p>Kính gửi ${order.user.fullName},</p>
        <p>Cảm ơn bạn đã đặt hàng! Chúng tôi xin xác nhận đã nhận được đơn hàng và đang xử lý.</p>
        
        <div class="order-details">
            <p><strong>Mã Đơn Hàng:</strong> ${order.orderCode}</p>
            <p><strong>Ngày Đặt Hàng:</strong> ${formattedDate}</p>
            <p><strong>Phương Thức Thanh Toán:</strong> ${paymentMethodDisplay}</p>
            <p><strong>Địa Chỉ Giao Hàng:</strong> ${order.shippingAddress}</p>
            
            <h3>Chi Tiết Đơn Hàng:</h3>
            <#list order.orderDetails as item>
            <div class="order-item">
                <p><strong>${item.productName}</strong> x ${item.quantity}</p>
                <p>Đơn Giá: ${item.priceAtPurchase}đ</p>
                <p>Thành Tiền: ${item.priceAtPurchase * item.quantity}đ</p>
            </div>
            </#list>
            
            <div class="total">
                <p>Tạm Tính: ${order.subtotal}đ</p>
                <#if order.discountAmount?? && order.discountAmount gt 0>
                <p>Giảm Giá: -${order.discountAmount}đ</p>
                </#if>
                <p>Phí Vận Chuyển: ${order.shippingFee}đ</p>
                <p>Tổng Cộng: ${order.totalAmount}đ</p>
            </div>
        </div>
        
        <p>Chúng tôi sẽ thông báo cho bạn khi đơn hàng được vận chuyển. Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với dịch vụ khách hàng của chúng tôi.</p>
        <p>Cảm ơn bạn đã mua sắm tại Gadgetify!</p>
    </div>
    <div class="footer">
        <p>&copy; ${.now?string("yyyy")} Gadgetify. Tất cả các quyền được bảo lưu.</p>
    </div>
</body>
</html>
