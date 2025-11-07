package com.ecom.util;

import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.security.Principal;

@Component
public class CommonUtil {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserService userService;
    public Boolean sendMail(String name ,String url, String reciepentEmail) throws MessagingException, UnsupportedEncodingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("vietmtp2004@gmail.com", "Ecommerce Development Team");
        helper.setTo(reciepentEmail);
        String content = """
<html>
  <body style="font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 30px;">
    <table width="100%%" cellpadding="0" cellspacing="0" style="max-width: 600px; margin: auto; background-color: #ffffff; border-radius: 8px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);">
      <tr>
        <td style="padding: 20px 30px;">
          <h2 style="color: #333333;">🔐 Reset Your Password</h2>
          <p style="font-size: 16px; color: #555555;">
            Xin chào <b>%s</b>,
          </p>
          <p style="font-size: 16px; color: #555555;">
            Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu từ bạn. Nhấn vào nút bên dưới để tiếp tục:
          </p>
          <div style="text-align: center; margin: 30px 0;">
            <a href="%s" style="background-color: #28a745; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">
              Đặt lại mật khẩu
            </a>
          </div>
          <p style="font-size: 14px; color: #777777;">
            Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này.
          </p>
          <hr style="margin: 30px 0; border: none; border-top: 1px solid #eeeeee;">
          <p style="font-size: 12px; color: #aaaaaa; text-align: center;">
            © 2025 Shopping Cart. All rights reserved.
          </p>
        </td>
      </tr>
    </table>
  </body>
</html>
""".formatted(name, url);

        helper.setSubject("Reset Password");
        helper.setText(content, true);
        mailSender.send(message);
        return true;
    }

    public static String generateUrl(HttpServletRequest request) {

        String siteUrl = request.getRequestURL().toString();

        return siteUrl.replace(request.getServletPath(), "");

    }

    String msg = null;
    public Boolean sendMailForProdcutOrder(ProductOrder order, String status) throws MessagingException, UnsupportedEncodingException {
        msg="<p>Dear [[name]],</p>"
        + "<p>Thank you for shopping with us. Your order status is now <b>[[orderStatus]]</b></p>"
        + "<p><b>Product Details:</b></p>"
        + "<p>Name : [[productName]]</p>"
        + "<p>Category : [[category]]</p>"
        + "<p>Quantity : [[quantity]]</p>"
        + "<p>Price : [[price]]</p>"
        + "<p>Payment Method : [[paymentType]]</p>"

        ;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("vietmtp2004@gmail.com", "Ecommerce Shopping");
        helper.setTo(order.getOrderAddress().getEmail());

        msg=msg.replace("[[name]]", order.getOrderAddress().getFirstName());
        msg=msg.replace("[[orderStatus]]", status);
        msg=msg.replace("[[productName]]", order.getProduct().getTitle());
        msg=msg.replace("[[category]]", order.getProduct().getCategory());
        msg=msg.replace("[[quantity]]", order.getQuantity().toString());
        msg=msg.replace("[[price]]", order.getProduct().getPrice().toString());
        msg=msg.replace("[[paymentType]]", order.getPaymentType());

        helper.setSubject("Product Order Status");
        helper.setText(msg, true);
        mailSender.send(message);
        return true;
    }

    public UserDtls getLoggedInUserDetail(Principal p) {
        String email = p.getName();
        UserDtls userDtls = userService.getUserByEmail(email);
        return userDtls;
    }
}
