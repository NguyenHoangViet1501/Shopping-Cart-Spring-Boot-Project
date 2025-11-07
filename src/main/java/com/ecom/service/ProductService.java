package com.ecom.service;

import com.ecom.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {

    public Product saveProduct(Product product);

    public List<Product> getAllProducts();

    public Boolean deleteProduct(int id);

    public Product getProductById(int id);

    public Product updateProduct(Product product, MultipartFile image);

    public List<Product> getAllActiveProducts(String category);

    public List<Product> searchProduct(String ch);

    public Page<Product> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category);

    public Page<Product> searchProductPagination(Integer pageNo, Integer pageSize, String ch);

    Page<Product> getAllProductPagination(Integer pageNo, Integer pageSize);

    public Page<Product> searchActiveProductPagination(Integer pageNo, Integer pageSize, String category, String ch);
}
