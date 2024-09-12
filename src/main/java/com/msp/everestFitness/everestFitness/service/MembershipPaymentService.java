package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.model.MembershipPayments;

import java.util.List;
import java.util.UUID;

public interface MembershipPaymentService {
    void addMembershipPayment(MembershipPayments membershipPayments);
    List<MembershipPayments> getAllMembershipPayments();
    MembershipPayments getMembershipPaymentById(UUID membershipPaymentId);

}
