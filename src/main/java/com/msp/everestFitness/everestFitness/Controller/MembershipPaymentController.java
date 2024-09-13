package com.msp.everestFitness.everestFitness.Controller;

import com.msp.everestFitness.everestFitness.model.MembershipPayments;
import com.msp.everestFitness.everestFitness.service.MembershipPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/membership-payment")
public class MembershipPaymentController {
    @Autowired
    private MembershipPaymentService membershipPaymentService;

    @PostMapping("/")
    public ResponseEntity<?> addMembershipPayment(@RequestBody MembershipPayments payments) {
        membershipPaymentService.addMembershipPayment(payments);
        return new ResponseEntity<>("Membership payment created successfully", HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<?> getMembershipPayment(@RequestParam(name = "paymentId", required = false) UUID paymentId) {
        if (paymentId != null) {
            return new ResponseEntity<>(membershipPaymentService.getMembershipPaymentById(paymentId), HttpStatus.OK);
        }
        return new ResponseEntity<>(membershipPaymentService.getAllMembershipPayments(), HttpStatus.OK);
    }

}