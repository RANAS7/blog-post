package com.msp.everestFitness.everestFitness.Controller;

import com.msp.everestFitness.everestFitness.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/order-item")
public class OrderItemController {
    @Autowired
    private OrderItemService orderItemService;

    @GetMapping("/")
    public ResponseEntity<?> getOrderItemsByOrderId(@RequestParam UUID orderId){
        return new ResponseEntity<>(orderItemService.getOrderItemsOfOrder(orderId), HttpStatus.OK);
    }
}
