package com.ecom.controller;

import com.ecom.model.Cart;
import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.OrderService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.management.loading.PrivateClassLoader;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home(){
        return "user/home";
    }

    @GetMapping("/addCart")
    public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid, HttpSession session){
        Cart saveCart = cartService.saveCart(pid, uid);
        if(ObjectUtils.isEmpty(saveCart)){
            session.setAttribute("errorMsg", "Product add to cart failed. Please try again.");
        }else {
            session.setAttribute("succMsg", "Product added to cart successfully.");
        }
        return "redirect:/product/"+pid;
    }
    @GetMapping("/cart")
    public String loadCartPage(Principal p, Model m){
        UserDtls user = getLoggedInUserDetail(p);
        List<Cart> carts = cartService.getCartByUser(user.getId());
        m.addAttribute("carts", carts);
        if (carts.size() > 0) {
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "/user/cart";
    }

    @GetMapping("/cartQuantityUpdate")
    public String UpdateCartQuantity(@RequestParam String sy, @RequestParam Integer cid){

        cartService.updateQuantity(sy, cid);
        return  "redirect:/user/cart";
    }

    private UserDtls getLoggedInUserDetail(Principal p) {
        String email = p.getName();
        UserDtls userDtls = userService.getUserByEmail(email);
        return userDtls;
    }

    @GetMapping("/orders")
    public String orderPage(Principal p, Model m){
        UserDtls user = getLoggedInUserDetail(p);
        List<Cart> carts = cartService.getCartByUser(user.getId());
        m.addAttribute("carts", carts);
        if (carts.size() > 0) {
            Double orderPrice = carts.get(carts.size() - 1).getTotalOrderPrice();
            Double totalOrderPrice = carts.get(carts.size() - 1).getTotalOrderPrice() + 25000 + 10000;
            m.addAttribute("orderPrice", orderPrice);
            m.addAttribute("totalOrderPrice", totalOrderPrice);
        }
        return "user/order";
    }

    @PostMapping("/save-order")
    public String saveOrder(@ModelAttribute OrderRequest request, Principal p) throws MessagingException, UnsupportedEncodingException {
        // System.out.println(request);
        UserDtls user = getLoggedInUserDetail(p);
        orderService.saveOrder(user.getId(), request);
        return "redirect:/user/success";
    }

    @GetMapping("/success")
    public String loadSuccess(){
        return "user/success";
    }

    @GetMapping("/user-orders")
    public String myOrder(Model m, Principal p){
        UserDtls loggedInUser = getLoggedInUserDetail(p);
        List<ProductOrder> orders = orderService.getOrderByUser(loggedInUser.getId());
        m.addAttribute("orders", orders);
        return "user/my_orders";
    }

    @GetMapping("/update-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session) throws MessagingException, UnsupportedEncodingException {

        OrderStatus[] values = OrderStatus.values();
        String status = null;
        for(OrderStatus orderSt:values){
            if(orderSt.getId().equals(st)){
                status = orderSt.getName();
            }
        }

        ProductOrder updateOrder = orderService.updateOrderStatus(id,status);
        try {
            commonUtil.sendMailForProdcutOrder(updateOrder, status);
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
        }
        if(!ObjectUtils.isEmpty(updateOrder)){
            session.setAttribute("succMsg", "Order status updated successfully.");
        } else {
            session.setAttribute("errorMsg", "Order status update failed. Please try again.");
        }
        return "redirect:/user/user-orders";
    }

    @GetMapping("/profile")
    public String profile(){
        return "user/profile";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img,HttpSession session){
        UserDtls updateUserProfile = userService.updateUserProfile(user, img);
        if(ObjectUtils.isEmpty(updateUserProfile)){
            session.setAttribute("errorMsg", "Profile update failed. Please try again.");
        }else {
            session.setAttribute("succMsg", "Profile updated successfully.");
        }
        return "redirect:/user/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword,@RequestParam String confirmPassword ,@RequestParam String currentPassword, Principal p, HttpSession session){
        UserDtls loggedInUserDetail = getLoggedInUserDetail(p);
        boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetail.getPassword());

        // 1. Kiểm tra mật khẩu hiện tại
        if (!matches) {
            session.setAttribute("errorMsg", "Current password is incorrect.");
            return "redirect:/user/profile";
        }

        // 2. Kiểm tra newPassword = confirmPassword
        if (!newPassword.equals(confirmPassword)) {
            session.setAttribute("errorMsg", "New password and Confirm password do not match.");
            return "redirect:/user/profile";
        }

        // 3. Cập nhật mật khẩu mới
        String encodePassword = passwordEncoder.encode(newPassword);
        loggedInUserDetail.setPassword(encodePassword);
        UserDtls updateUser = userService.updateUser(loggedInUserDetail);

        if (ObjectUtils.isEmpty(updateUser)) {
            session.setAttribute("errorMsg", "Password change failed. Please try again.");
        } else {
            session.setAttribute("succMsg", "Password changed successfully.");
        }
        return "redirect:/user/profile";
    }
}
