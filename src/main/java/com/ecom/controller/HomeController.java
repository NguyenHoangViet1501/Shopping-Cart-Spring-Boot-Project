package com.ecom.controller;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.UserDtls;
import com.ecom.repository.ProductRepository;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import io.micrometer.common.util.StringUtils;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;


@Controller
public class HomeController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CartService cartService;

    @GetMapping("/")
    public String index(Model m) {

        List<Category> allActiveCategory = categoryService.getAllActiveCategory().stream()
                .sorted((p1,p2)-> Integer.compare(p2.getId(),p1.getId()))
                .limit(6).toList();
        List<Product> allActiveProduct = productService.getAllActiveProducts("").stream()
                .sorted((p1,p2)->Integer.compare(p2.getId(),p1.getId()))
                .limit(8).toList();
        m.addAttribute("category", allActiveCategory);
        m.addAttribute("products", allActiveProduct);

        return "index";
    }

    @GetMapping("/signin")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/products")
    public String products(Model m, @RequestParam(value = "category", defaultValue = "") String category,
                           @RequestParam(name="pageNo", defaultValue = "0") Integer pageNo,
                           @RequestParam(name="pageSize", defaultValue = "9") Integer pageSize,
                           @RequestParam(defaultValue = "") String ch ) {
        List<Category> categories = categoryService.getAllActiveCategory();
        m.addAttribute("paramValue", category);
        m.addAttribute("categories", categories);


//        List<Product> products = productService.getAllActiveProducts(category);
//        m.addAttribute("products", products);

        Page<Product> page = null;
        if(StringUtils.isEmpty(ch)){
            page = productService.getAllActiveProductPagination(pageNo,pageSize, category);
        }
        else {
            page = productService.searchActiveProductPagination(pageNo, pageSize, category, ch);
        }

        List<Product> products = page.getContent();
        m.addAttribute("products", products);
        m.addAttribute("productsSize", products.size());
        m.addAttribute("pageNo", page.getNumber());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());
        m.addAttribute("pageSize", pageSize);
        return "product";
    }

    @GetMapping("/product/{id}")
    public String product(@PathVariable int id, Model m) {
        Product productById = productService.getProductById(id);
        m.addAttribute("product", productById);
        return "view_product";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
            throws IOException {

        Boolean existsEmail = userService.existEmail(user.getEmail());
        if(existsEmail){
            session.setAttribute("errorMsg", "Email already exists");
            return "redirect:/register";
        }else{
            String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
            user.setProfileImage(imageName);
            UserDtls saveUser = userService.saveUser(user);

            if (!ObjectUtils.isEmpty(saveUser)) {
                if (!file.isEmpty()) {
                    File saveFile = new ClassPathResource("static/img").getFile();
                    File profileImgDir = new File(saveFile.getAbsolutePath() + File.separator + "profile_img");
                    if (!profileImgDir.exists()) {
                        profileImgDir.mkdirs();
                    }
                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "profile_img" + File.separator
                            + file.getOriginalFilename());

                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                }
                session.setAttribute("succMsg", "Register successfully");
            } else {
                session.setAttribute("errorMsg", "Something wrong on server");
            }
        }

        return "redirect:/register";
    }

    //Forgot Password
    @GetMapping("/forgot_password")
    public String showForgotPassword(){
        return "forgot_password";
    }

    @PostMapping("/forgot_password")
    public String processForgotPassword(@RequestParam String email, HttpSession session, HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {

        UserDtls userByEmail = userService.getUserByEmail(email);

        if(ObjectUtils.isEmpty(userByEmail)){
            session.setAttribute("errorMsg", "Email not found");

        }else {

            String resetToken = UUID.randomUUID().toString();

            userService.updateUserResetToken(email, resetToken);

            String name = userByEmail.getName();
            //Generate UTL : /reset_password?token=resetToken

            String url = CommonUtil.generateUrl(request)+ "/reset_password?token=" + resetToken;
            Boolean sendMail = commonUtil.sendMail(name,url,email);

            if(sendMail){
                session.setAttribute("succMsg", "Password reset link sent to your email");
            }else{
                session.setAttribute("errorMsg", "Failed to send email, please try again later");
            }
        }

        return "redirect:/forgot_password";
    }
    @GetMapping("/reset_password")
    public String showResetPassword(@RequestParam String token,  Model m, HttpSession session) {
        UserDtls userByToken = userService.getUserByToken(token);

        if (userByToken == null) {
            m.addAttribute("msg", "Your link is invalid or expired !!");
            return "message";
        }
        m.addAttribute("token", token);
        return "reset_password";
    }

    @PostMapping("/reset_password")
    public String resetPassword(@RequestParam String token, @RequestParam String password, HttpSession session, Model m) {
        UserDtls userByToken = userService.getUserByToken(token);

        if (userByToken == null) {
            m.addAttribute("errorMsg", "Your link is invalid or expired !!");
            return "message";
        }else {
            userByToken.setPassword(passwordEncoder.encode(password));
            userByToken.setResetToken(null);
            userService.updateUser(userByToken);
            session.setAttribute("succMsg", "Password reset successfully, you can login now");
            m.addAttribute("msg", "Password reset successfully, you can login now");
            return "message";

        }
    }

    @GetMapping("/search")
    public String searchProduct(@RequestParam String ch, Model m){
        List<Product> searchProduct = productService.searchProduct(ch);
        List<Category> categories = categoryService.getAllActiveCategory();
        m.addAttribute("categories", categories);
        m.addAttribute("products", searchProduct);
        return "product";
    }
}
