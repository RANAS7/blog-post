
package com.msp.everestFitness.everestFitness.service.impl;

import com.msp.everestFitness.everestFitness.config.LoginUtil;
import com.msp.everestFitness.everestFitness.enumrated.UserType;
import com.msp.everestFitness.everestFitness.exceptions.ResourceNotFoundException;
import com.msp.everestFitness.everestFitness.model.Members;
import com.msp.everestFitness.everestFitness.model.Users;
import com.msp.everestFitness.everestFitness.repository.MembersRepo;
import com.msp.everestFitness.everestFitness.repository.UsersRepo;
import com.msp.everestFitness.everestFitness.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MembersRepo membersRepo;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private LoginUtil loginUtil;

    @Override
    public void createMember(Members members) {
        if (loginUtil.getCurrentUserId() != null) {
            Members members1 = membersRepo.findById(loginUtil.getCurrentUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("The member not found with the memberId: " + loginUtil.getCurrentUserId()));
            members1.setAddress(members.getAddress());
            members1.setFirstName(members.getFirstName());
            members1.setLastName(members.getLastName());
            members.setDateOfBirth(members.getDateOfBirth());
            membersRepo.save(members1);
        }
        membersRepo.save(members);

        Users user = usersRepo.findById(members.getUsers().getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("The user not found with the userId: " + members.getUsers().getUserId()));
        user.setUserType(UserType.MEMBER);
        usersRepo.save(user);
    }

    @Override
    public List<Members> getAllMembers() {
        return membersRepo.findAll();
    }

    @Override
    public Members getMemberById(UUID memberId) {
        return membersRepo.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("The member not found with the memberId: " + memberId));
    }
}
