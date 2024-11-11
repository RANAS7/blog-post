package com.msp.everestFitness.everestFitness.dto;

import com.msp.everestFitness.everestFitness.model.Orders;
import com.msp.everestFitness.everestFitness.model.ShippingInfo;
import lombok.Data;

@Data
public class GuestOrderRequest {
    private Orders orders;
    private ShippingInfo shippingInfo;
}
