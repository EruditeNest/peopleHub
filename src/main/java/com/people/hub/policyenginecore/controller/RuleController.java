package com.people.hub.policyenginecore.controller;

import com.people.hub.common.RestApiResponse;
import com.people.hub.policyenginecore.dto.RuleRequestDto;
import com.people.hub.policyenginecore.model.Rule;
import com.people.hub.policyenginecore.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rule")
public class RuleController {
    private final RuleService ruleService;

    @PreAuthorize("@auth.hasPermission(authentication, 'RULE_CREATE')")
    @PostMapping("/create")
    public ResponseEntity<Rule> createRule(@RequestBody RuleRequestDto requestDto) {
        Rule rule = ruleService.createRule(requestDto);
        return ResponseEntity.status(201).body(rule);
    }

    @PreAuthorize("@auth.hasPermission(authentication, 'RULE_UPDATE')")
    @PostMapping("/update/{id}")
    public ResponseEntity<Rule> updateRule(@PathVariable Long id, @RequestBody RuleRequestDto requestDto) {
        Rule rule = ruleService.updateRule(id, requestDto);
        return ResponseEntity.ok(rule);
    }

    @PreAuthorize("@auth.hasPermission(authentication, 'RULE_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<Rule> getRuleById(@PathVariable Long id){
        return ResponseEntity.ok(ruleService.getRuleById(id));
    }

    @PreAuthorize("@auth.hasPermission(authentication, 'RULE_READ')")
    @GetMapping("/policy/{policyId}")
    public ResponseEntity<List<Rule>> getAllRulesByPolicyId(@PathVariable Long policyId){
        return ResponseEntity.ok(ruleService.getAllRulesByPolicyId(policyId));
    }

    @PreAuthorize("@auth.hasPermission(authentication, 'RULE_READ')")
    @GetMapping("/policy/{policyId}/active")
    public ResponseEntity<List<Rule>> getAllActiveRulesByPolicyId(@PathVariable Long policyId) {
        return ResponseEntity.ok(ruleService.getAllActiveRulesByPolicyId(policyId));
    }

    @PreAuthorize("@auth.hasPermission(authentication, 'RULE_DELETE')")
    @PostMapping("/delete/{id}")
    public ResponseEntity<RestApiResponse> deleteRuleById(@PathVariable Long id) {
        return ResponseEntity.ok(ruleService.deleteRuleById(id));
    }
}
