package com.msp.everestFitness.service.impl;

import com.msp.everestFitness.dto.OrderItemWithImageDto;
import com.msp.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.model.OrderItems;
import com.msp.everestFitness.model.ProductImages;
import com.msp.everestFitness.model.Products;
import com.msp.everestFitness.repository.OrderItemsRepo;
import com.msp.everestFitness.repository.ProductsImagesRepo;
import com.msp.everestFitness.repository.ProductsRepo;
import com.msp.everestFitness.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderItemServiceImpl implements OrderItemService {
    @Autowired
    private OrderItemsRepo orderItemsRepo;

    @Autowired
    private ProductsImagesRepo productsImagesRepo;

    @Autowired
    private ProductsRepo productsRepo;

    @Override
    public List<OrderItemWithImageDto> getOrderItemsOfOrder(UUID orderId) {
        List<OrderItems> orderItemsList= orderItemsRepo.findByOrder_OrderId(orderId);
        List<OrderItemWithImageDto> dtoList=new ArrayList<>();
        for (OrderItems item: orderItemsList){
            Products products=productsRepo.findById(item.getProducts().getProductId())
                    .orElseThrow(()-> new ResourceNotFoundException("Product not found with the id: "+item.getProducts().getProductId()));

            ProductImages productImages=productsImagesRepo.findByProduct_ProductId(products.getProductId()).getFirst();

            OrderItemWithImageDto dto = new OrderItemWithImageDto();
            dto.setOrderItemId(item.getOrderItemId());
            dto.setProductId(products.getProductId());
            dto.setProductName(products.getName());
            dto.setPrice(item.getPrice());
            dto.setQuantity(Long.valueOf(item.getQuantity()));
            dto.setTotalAmt(item.getTotalAmt());
            dto.setImageUrl(productImages.getImageUrl());

            dtoList.add(dto);
        }
        return dtoList;
    }
}
