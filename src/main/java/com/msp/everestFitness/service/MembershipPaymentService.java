package com.msp.everestFitness.service;

import com.msp.everestFitness.model.MembershipPayments;

import java.util.List;
import java.util.UUID;

public interface MembershipPaymentService {
    void addMembershipPayment(MembershipPayments membershipPayments);
    List<MembershipPayments> getAllMembershipPayments();
    MembershipPayments getMembershipPaymentById(UUID membershipPaymentId);

}
