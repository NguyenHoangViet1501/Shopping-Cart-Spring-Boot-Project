package com.ecom.config;

import com.ecom.model.Category;
import com.ecom.model.UserDtls;
import com.ecom.service.CartService;
import com.ecom.service.CategoryService;
import com.ecom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CartService cartService;

    @ModelAttribute
    public void addUserToModel(Principal principal, Model model) {
        if (principal != null) {
            String email = principal.getName();
            UserDtls user = userService.getUserByEmail(email);
            model.addAttribute("user", user);
            Integer countCart = cartService.getCountCart(user.getId());
            model.addAttribute("countCart", countCart);
        }
        else {
            model.addAttribute("user", null);
        }
        List<Category> allActiveCategory = categoryService.getAllActiveCategory();
        model.addAttribute("categorys", allActiveCategory);
    }
}