package com.people.hub.policyenginecore.controller;

import com.people.hub.common.RestApiResponse;
import com.people.hub.policyenginecore.dto.PolicyRequestDto;
import com.people.hub.policyenginecore.model.Policy;
import com.people.hub.policyenginecore.service.PolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/policy")
public class PolicyController {

    private final PolicyService policyService;

    @PreAuthorize("@auth.hasPermission(authentication, 'POLICY_CREATE')")
    @PostMapping("/create")
    public ResponseEntity<Policy> createPolicy(@RequestBody PolicyRequestDto requestDto) {
        return ResponseEntity.status(201).body(policyService.createPolicy(requestDto));
    }

    @PreAuthorize("@auth.hasPermission(authentication, 'POLICY_UPDATE')")
    @PostMapping("/update/{id}")
    public ResponseEntity<Policy> updatePolicy(@PathVariable Long id, @RequestBody PolicyRequestDto requestDto) {
        return ResponseEntity.ok(policyService.updatePolicy(id, requestDto));
    }

    @PreAuthorize("@auth.hasPermission(authentication, 'POLICY_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Policy> getPolicyById(@PathVariable Long id){
        return ResponseEntity.ok(policyService.getPolicyById(id));
    }

    @PreAuthorize("@auth.hasPermission(authentication, 'POLICY_READ')")
    @GetMapping("/service-code/{serviceCode}")
    public ResponseEntity<List<Policy>> getPolicyByServiceCode(@PathVariable String serviceCode) {
        return ResponseEntity.ok(policyService.getPolicyByServiceCode(serviceCode));
    }

    @PreAuthorize("@auth.hasPermission(authentication, 'POLICY_READ')")
    @GetMapping("/service-code/{decisionCode}")
    public ResponseEntity<List<Policy>> getPolicyByDecisionCode(@PathVariable String decisionCode) {
        return ResponseEntity.ok(policyService.getPolicyByDecisionCode(decisionCode));
    }

    @PreAuthorize("@auth.hasPermission(authentication, 'POLICY_READ')")
    @GetMapping("/service-code/{contextCode}")
    public ResponseEntity<List<Policy>> getPolicyByContextCode(@PathVariable String contextCode) {
        return ResponseEntity.ok(policyService.getPolicyByContextCode(contextCode));
    }

    @PreAuthorize("@auth.hasPermission(authentication, 'POLICY_DELETE')")
    @PostMapping("/delete/{id}")
    public ResponseEntity<RestApiResponse> deletePolicyById(@PathVariable Long id) {
        return ResponseEntity.ok(policyService.deletePolicyById(id));
    }
}
