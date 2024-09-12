package com.msp.everestFitness.everestFitness.service;

import com.msp.everestFitness.everestFitness.model.Members;

import java.util.List;
import java.util.UUID;

public interface MemberService {
    void createMember(Members members);
    List<Members> getAllMembers();
    Members getMemberById(UUID memberId);
}
