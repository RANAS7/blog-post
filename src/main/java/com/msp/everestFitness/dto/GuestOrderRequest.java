package com.msp.everestFitness.dto;

import com.msp.everestFitness.model.Orders;
import com.msp.everestFitness.model.ShippingInfo;
import lombok.Data;

@Data
public class GuestOrderRequest {
    private Orders orders;
    private ShippingInfo shippingInfo;
}
