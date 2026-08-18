package com.people.hub.policyenginecore.service;

import com.people.hub.common.exception.NotFoundException;
import com.people.hub.policyenginecore.model.Policy;
import com.people.hub.policyenginecore.repository.PolicyRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PolicyService {
    private final PolicyRepo policyRepo;

    public Policy getPolicyById(Long id){
        return policyRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Policy not found", id));
    }
}
