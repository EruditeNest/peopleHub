package com.people.hub.policyenginecore.controller;

import com.people.hub.policyenginecore.dto.RuleRequestDto;
import com.people.hub.policyenginecore.model.Rule;
import com.people.hub.policyenginecore.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/rule")
public class RuleController {
    private final RuleService ruleService;

    public ResponseEntity<Rule> createRule(@RequestBody RuleRequestDto requestDto) {
        Rule rule = ruleService.createRule(requestDto);
        return ResponseEntity.status(201).body(rule);
    }
}
