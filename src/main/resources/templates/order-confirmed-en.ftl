<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Order Confirmation - Gadgetify</title>
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
        <h2>Order Confirmation</h2>
        <p>Dear ${order.user.fullName},</p>
        <p>Thank you for your order! We're pleased to confirm that we've received your order and it's being processed.</p>
        
        <div class="order-details">
            <p><strong>Order Code:</strong> ${order.orderCode}</p>
            <p><strong>Order Date:</strong> ${formattedDate}</p>
            <p><strong>Payment Method:</strong> ${paymentMethodDisplay}</p>
            <p><strong>Shipping Address:</strong> ${order.shippingAddress}</p>
            
            <h3>Order Items:</h3>
            <#list order.orderDetails as item>
            <div class="order-item">
                <p><strong>${item.productName}</strong> x ${item.quantity}</p>
                <p>Unit Price: $${item.priceAtPurchase}</p>
                <p>Subtotal: $${item.priceAtPurchase * item.quantity}</p>
            </div>
            </#list>
            
            <div class="total">
                <p>Subtotal: $${order.subtotal}</p>
                <#if order.discountAmount?? && order.discountAmount gt 0>
                <p>Discount: -$${order.discountAmount}</p>
                </#if>
                <p>Shipping: $${order.shippingFee}</p>
                <p>Total: $${order.totalAmount}</p>
            </div>
        </div>
        
        <p>We'll notify you when your order has been shipped. If you have any questions, please contact our customer service.</p>
        <p>Thank you for shopping with Gadgetify!</p>
    </div>
    <div class="footer">
        <p>&copy; ${.now?string("yyyy")} Gadgetify. All rights reserved.</p>
    </div>
</body>
</html>
