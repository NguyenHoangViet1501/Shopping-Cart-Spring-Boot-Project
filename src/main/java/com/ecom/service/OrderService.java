package com.ecom.service;

import com.ecom.model.OrderRequest;
import com.ecom.model.ProductOrder;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;

import java.io.UnsupportedEncodingException;
import java.util.List;

public interface OrderService {

    public void saveOrder(Integer userId, OrderRequest orderRequest) throws MessagingException, UnsupportedEncodingException;

    public List<ProductOrder> getOrderByUser(Integer userId);

    public ProductOrder updateOrderStatus(Integer orderId, String status);

    public List<ProductOrder> getAllOrders();

    public ProductOrder getOrderByOrderId(String orderId);

    public Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize);


}
