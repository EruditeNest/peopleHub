package com.people.hub.policyenginecore.controller;

import com.people.hub.common.RestApiResponse;
import com.people.hub.policyenginecore.dto.RuleRequestDto;
import com.people.hub.policyenginecore.model.Rule;
import com.people.hub.policyenginecore.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rule")
public class RuleController {
    private final RuleService ruleService;

    @PostMapping("/create")
    public ResponseEntity<Rule> createRule(@RequestBody RuleRequestDto requestDto) {
        Rule rule = ruleService.createRule(requestDto);
        return ResponseEntity.status(201).body(rule);
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<Rule> updateRule(@PathVariable Long id, @RequestBody RuleRequestDto requestDto) {
        Rule rule = ruleService.updateRule(id, requestDto);
        return ResponseEntity.ok(rule);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rule> getRuleById(@PathVariable Long id){
        return ResponseEntity.ok(ruleService.getRuleById(id));
    }

    @GetMapping("/policy-id/{id}")
    public ResponseEntity<List<Rule>> getAllRulesByPolicyId(@PathVariable Long policyId){
        return ResponseEntity.ok(ruleService.getAllRulesByPolicyId(policyId));
    }

    @GetMapping("/active-policy-id/{id}")
    public ResponseEntity<List<Rule>> getAllActiveRulesByPolicyId(@PathVariable Long policyId) {
        return ResponseEntity.ok(ruleService.getAllActiveRulesByPolicyId(policyId));
    }

    @GetMapping("/delete/{id}")
    public ResponseEntity<RestApiResponse> deleteRuleById(@PathVariable Long id) {
        return ResponseEntity.ok(ruleService.deleteRuleById(id));
    }
}
