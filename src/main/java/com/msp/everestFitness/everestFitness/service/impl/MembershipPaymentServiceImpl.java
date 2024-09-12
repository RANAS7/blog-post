package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.enumrated.MembershipStatus;
import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.Members;
import com.msp.everestFitness.everestFitness.model.MembershipPayments;
import com.msp.everestFitness.everestFitness.repository.MembersRepo;
import com.msp.everestFitness.everestFitness.repository.MembershipPaymentsRepo;
import com.msp.everestFitness.everestFitness.service.MembershipPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MembershipPaymentServiceImpl implements MembershipPaymentService {

    @Autowired
    private MembershipPaymentsRepo membershipPaymentsRepo;

    @Autowired
    private MembersRepo membersRepo;

    @Override
    public void addMembershipPayment(MembershipPayments payments) {
        if (payments.getMembershipPaymentId() != null) {
            MembershipPayments payment = membershipPaymentsRepo.findById(payments.getMembershipPaymentId())
                    .orElseThrow(() -> new ResourceNotFoundException("The membership payment not found with the membership payment id: " + payments.getMembershipPaymentId()));
            payment.setPaymentMethod(payments.getPaymentMethod());
            payment.setAmount(payments.getAmount());
            membershipPaymentsRepo.save(payment);
        }

        membershipPaymentsRepo.save(payments);

        Members members = membersRepo.findById(payments.getMember().getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("The member not found with the member id: " + payments.getMember().getMemberId()));
        members.setMembershipStatus(MembershipStatus.ACTIVE);
        membersRepo.save(members);
    }

    @Override
    public List<MembershipPayments> getAllMembershipPayments() {
        return membershipPaymentsRepo.findAll();
    }

    @Override
    public MembershipPayments getMembershipPaymentById(UUID membershipPaymentId) {
        return membershipPaymentsRepo.findById(membershipPaymentId)
                .orElseThrow(() -> new ResourceNotFoundException("The membership payment not found with the Id: "+membershipPaymentId));
    }
}