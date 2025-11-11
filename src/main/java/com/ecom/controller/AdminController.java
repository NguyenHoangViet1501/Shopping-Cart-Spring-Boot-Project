package com.ecom.controller;

import com.ecom.model.Category;
import com.ecom.model.Product;
import com.ecom.model.ProductOrder;
import com.ecom.model.UserDtls;
import com.ecom.service.CategoryService;
import com.ecom.service.OrderService;
import com.ecom.service.ProductService;
import com.ecom.service.UserService;
import com.ecom.util.CommonUtil;
import com.ecom.util.OrderStatus;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommonUtil commonUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index() {
        return "admin/index";
    }

    @GetMapping("/loadAddProduct")
    public String loadAddProduct(Model m) {
        List<Category> categoryList = categoryService.getAllCategories();
        m.addAttribute("categories", categoryList);
        return "admin/add_product";
    }

    @GetMapping("/category")
    public String category(Model m, @RequestParam(name="pageNo", defaultValue = "0") Integer pageNo,
                           @RequestParam(name="pageSize", defaultValue = "4") Integer pageSize) {
        //m.addAttribute("categorys", categoryService.getAllCategories());
        Page<Category> page = categoryService.getAllCategoryPagination(pageNo, pageSize);
        List<Category> categorys = page.getContent();
        m.addAttribute("categorys", categorys);
        m.addAttribute("pageNo", page.getNumber());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());
        m.addAttribute("pageSize", pageSize);
        return "admin/category";
    }

    @PostMapping("/saveCategory")
    public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException{

        String imageName = file != null ? file.getOriginalFilename(): "default.jpg";
        category.setImageName(imageName);


        Boolean existCategory = categoryService.existCategory(category.getName());

        if (existCategory) {
            session.setAttribute("errorMsg", "Category already exists");
        }else{
            Category saveCategory = categoryService.saveCategory(category);
            if(ObjectUtils.isEmpty(saveCategory)){
                session.setAttribute("errorMsg", "Not saved ! internal server error");
            } else {

                File saveFile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+"category_img"+File.separator+file.getOriginalFilename());

                System.out.println(path);

                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                session.setAttribute("succMsg", "Saved successfully");
            }
        }

        return "redirect:/admin/category";
    }

    @GetMapping("/deleteCategory/{id}")
    public String deleteCategory(@PathVariable int id, HttpSession session) {
        Boolean deleteCategory = categoryService.deleteCategory(id);
        if(deleteCategory){
            session.setAttribute("succMsg", "Category deleted successfully");
        } else {
            session.setAttribute("errorMsg", "Category not found or could not be deleted");
        }
        return "redirect:/admin/category";
    }

    @GetMapping("/loadEditCategory/{id}")
    public String loadEditCategory(@PathVariable int id, Model m){
        m.addAttribute("category", categoryService.getCategoryById(id));
        return "admin/edit_category";
    }

    @PostMapping("/updateCategory")
    public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file, HttpSession session) throws IOException
    {
        Category oldCategory = categoryService.getCategoryById(category.getId());
        String imageName = file.isEmpty()?oldCategory.getImageName():file.getOriginalFilename();

        if(!ObjectUtils.isEmpty(category)) {
            oldCategory.setName(category.getName());
            oldCategory.setIsActive(category.getIsActive());
            oldCategory.setImageName(imageName);
        }

        Category updateCategory = categoryService.saveCategory(oldCategory);
        if(!ObjectUtils.isEmpty(updateCategory)){

            if(!file.isEmpty())
            {
                File saveFile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "category_img" + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            session.setAttribute("succMsg", "Category updated successfully");
        } else {
            session.setAttribute("errorMsg", "Category not updated ! internal server error");
        }
        return "redirect:/admin/loadEditCategory/"+category.getId();
    }

    @PostMapping("/saveProduct")
    public String saveProduct(@ModelAttribute Product product,@RequestParam("file") MultipartFile image , HttpSession session) throws IOException{

        String imageName =  image.isEmpty() ? "default.jpg": image.getOriginalFilename();
        product.setImage(imageName);
        product.setDiscount(0);
        product.setDiscountPrice(product.getPrice() - (product.getPrice() * product.getDiscount() / 100));
        Product saveProduct = productService.saveProduct(product);
        if(!ObjectUtils.isEmpty(saveProduct)){

            File saveFile = new ClassPathResource("static/img").getFile();
            Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + "product_img" + File.separator + image.getOriginalFilename());
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            session.setAttribute("succMsg", "Product saved successfully");
        } else {
            session.setAttribute("errorMsg", "Product not saved ! internal server error");
        }
        return "redirect:/admin/loadAddProduct";
    }

    @GetMapping("/products")
    public String loadViewProduct(Model m,@RequestParam(defaultValue = "") String ch, @RequestParam(name="pageNo", defaultValue = "0") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue = "2") Integer pageSize, HttpSession session){
        Page<Product> page = null;
        if(ch!=null && ch.length()>0){
            page = productService.searchProductPagination(pageNo, pageSize, ch);
        }else{
            page = productService.getAllProductPagination(pageNo, pageSize);
        }

        if(page == null || page.isEmpty()){
            session.setAttribute("errorMsg", "No products found");
        }else {
            m.addAttribute("products", page.getContent());

            m.addAttribute("pageNo", page.getNumber());
            m.addAttribute("totalPages", page.getTotalPages());
            m.addAttribute("totalElements", page.getTotalElements());
            m.addAttribute("isFirst", page.isFirst());
            m.addAttribute("isLast", page.isLast());
            m.addAttribute("pageSize", pageSize);
        }
        return "admin/product";
    }
    @GetMapping("/deleteProduct/{id}")
    public String deleteProduct(@PathVariable int id, HttpSession session) {
        Boolean deleteProduct = productService.deleteProduct(id);
        if(deleteProduct){
            session.setAttribute("succMsg", "Product deleted successfully");
        } else {
            session.setAttribute("errorMsg", "Product not found or could not be deleted");
        }
        return "redirect:/admin/products";
    }
    @GetMapping("/loadEditProduct/{id}")
    public String loadEditProduct(@PathVariable int id, Model m) {
        m.addAttribute("product", productService.getProductById(id));
        List<Category> categoryList = categoryService.getAllCategories();
        m.addAttribute("categories", categoryList);
        return "admin/edit_product";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute Product product,@RequestParam("file") MultipartFile image, HttpSession session, Model m) throws IOException{
        if(product.getDiscount()<0 || product.getDiscount()>100){
            session.setAttribute("errorMsg", "Discount must be between 0 and 100");
            return "redirect:/admin/loadEditProduct/"+product.getId();
        }
        Product updateProduct = productService.updateProduct(product, image);
        if(!ObjectUtils.isEmpty(updateProduct)) {
            session.setAttribute("succMsg", "Product updated successfully");
        } else {
            session.setAttribute("errorMsg", "Product not updated ! internal server error");
        }

        return "redirect:/admin/loadEditProduct/"+product.getId();
    }

    @GetMapping("/users")
    public String getAllUsers(Model m,@RequestParam Integer type) {
        List<UserDtls> users = null;
        if(type==1)
        {
            users = userService.getUsers("ROLE_USER");
        }else {
            users = userService.getUsers("ROLE_ADMIN");
        }
        m.addAttribute("userType",type);
        m.addAttribute("users",users);
        return "admin/users";
    }

    @GetMapping("/updateSts")
    public String updateUserAccountStatus(@RequestParam Boolean status, @RequestParam Integer id, @RequestParam Integer type, HttpSession session)
    {
        Boolean f = userService.updateUserAccountStatus(id, status);
        if(f){
            session.setAttribute("succMsg", "User account status updated successfully");
        } else {
            session.setAttribute("errorMsg", "User account status not updated ! internal server error");
        }
        return "redirect:/admin/users?type="+type;
    }

    @GetMapping("/orders")
    public String getAllOrders(Model m,@RequestParam(defaultValue = "") String ch, @RequestParam(name="pageNo", defaultValue = "0") Integer pageNo,
                               @RequestParam(name="pageSize", defaultValue = "4") Integer pageSize ) {
        Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
        m.addAttribute("orders", page.getContent());
        m.addAttribute("srch",false);


        m.addAttribute("pageNo", page.getNumber());
        m.addAttribute("totalPages", page.getTotalPages());
        m.addAttribute("totalElements", page.getTotalElements());
        m.addAttribute("isFirst", page.isFirst());
        m.addAttribute("isLast", page.isLast());
        m.addAttribute("pageSize", pageSize);
        return "admin/orders";
    }

    @PostMapping("/update-order-status")
    public String updateOrderStatus(@RequestParam Integer id, @RequestParam Integer st, HttpSession session){

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
        return "redirect:/admin/orders";
    }

    @GetMapping("/search-order")
    public String searchProduct(@RequestParam String orderId, Model m, HttpSession session,@RequestParam(defaultValue = "") String ch, @RequestParam(name="pageNo", defaultValue = "0") Integer pageNo,
                                @RequestParam(name="pageSize", defaultValue = "4") Integer pageSize) {

        if(orderId!=null && orderId.length() > 0) {
            ProductOrder order = orderService.getOrderByOrderId(orderId.trim());

            if (ObjectUtils.isEmpty(order)) {
                session.setAttribute("errorMsg", "Order not found");
                m.addAttribute("orderDtls", null);
            } else {
                m.addAttribute("orderDtls", order);
            }

            m.addAttribute("srch", true);
        }else{
            Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
            m.addAttribute("orders", page.getContent());
            m.addAttribute("srch",false);


            m.addAttribute("pageNo", page.getNumber());
            m.addAttribute("totalPages", page.getTotalPages());
            m.addAttribute("totalElements", page.getTotalElements());
            m.addAttribute("isFirst", page.isFirst());
            m.addAttribute("isLast", page.isLast());
            m.addAttribute("pageSize", pageSize);
        }
        return "/admin/orders";
    }

    @GetMapping("/add-admin")
    public String loadAdminAdd(){
        return "/admin/add_admin";
    }

    @PostMapping("/save-admin")
    public String saveAdmin(@ModelAttribute UserDtls user, @RequestParam("img") MultipartFile file, HttpSession session)
            throws IOException {

        String imageName = file.isEmpty() ? "default.jpg" : file.getOriginalFilename();
        user.setProfileImage(imageName);
        UserDtls saveUser = userService.saveAdmin(user);

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

        return "redirect:/admin/add-admin";
    }

    @GetMapping("/profile")
    public String profile(){
        return "admin/profile-admin";
    }

    @PostMapping("/update-profile")
    public String updateProfile(@ModelAttribute UserDtls user, @RequestParam MultipartFile img,HttpSession session){
        UserDtls updateUserProfile = userService.updateUserProfile(user, img);
        if(ObjectUtils.isEmpty(updateUserProfile)){
            session.setAttribute("errorMsg", "Profile update failed. Please try again.");
        }else {
            session.setAttribute("succMsg", "Profile updated successfully.");
        }
        return "redirect:/admin/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String newPassword,@RequestParam String confirmPassword ,@RequestParam String currentPassword, Principal p, HttpSession session){
        UserDtls loggedInUserDetail = commonUtil.getLoggedInUserDetail(p);
        boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetail.getPassword());

        // 1. Kiểm tra mật khẩu hiện tại
        if (!matches) {
            session.setAttribute("errorMsg", "Current password is incorrect.");
            return "redirect:/admin/profile";
        }

        // 2. Kiểm tra newPassword = confirmPassword
        if (!newPassword.equals(confirmPassword)) {
            session.setAttribute("errorMsg", "New password and Confirm password do not match.");
            return "redirect:/admin/profile";
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
        return "redirect:/admin/profile";
    }
}
