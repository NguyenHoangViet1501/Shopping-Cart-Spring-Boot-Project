package com.ecom.repository;

import org.springframework.stereotype.Repository;
import com.ecom.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    // Additional query methods can be defined here if needed
    public Cart findByProductIdAndUserId(Integer productId, Integer userId);

    public Integer countByUserId(Integer userId);


    public List<Cart> findByUserId(Integer userId);
}
