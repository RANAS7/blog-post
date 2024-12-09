package com.msp.everestFitness.Controller;

import com.msp.everestFitness.model.Members;
import com.msp.everestFitness.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/member")
public class MembersController {
    @Autowired
    private MemberService memberService;

    @PostMapping("/")
    public ResponseEntity<?> createMember(@RequestBody Members members) {
        memberService.createMember(members);
        return new ResponseEntity<>("Now you are registered as member", HttpStatus.CREATED);
    }

    @GetMapping("/")
    public ResponseEntity<?> getMembers(@RequestParam UUID memberId) {
        if (memberId != null) {
            return new ResponseEntity<>(memberService.getMemberById(memberId), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(memberService.getAllMembers(), HttpStatus.OK);
        }
    }
}