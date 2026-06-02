package com.people.hub.taskmanager.controller;

import com.people.hub.common.RestApiResponse;
import com.people.hub.taskmanager.dto.ClientDto;
import com.people.hub.taskmanager.model.Client;
import com.people.hub.taskmanager.service.ClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
@Slf4j
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody ClientDto dto) {

        return ResponseEntity.ok(clientService.createClient(dto));
    }

    @PostMapping("/{clientId}")
    public ResponseEntity<Client> updateClient(
            @PathVariable Long clientId,
            @RequestBody ClientDto dto) {

        return ResponseEntity.ok(clientService.updateClient(clientId, dto));
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<Client> getClientById(@PathVariable Long clientId) {

        return ResponseEntity.ok(clientService.getClientById(clientId));
    }

    @GetMapping
    public ResponseEntity<RestApiResponse> getClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortField,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        return ResponseEntity.ok(clientService.getClients(
            page,
            size,
            sortField,
            sortOrder)
        );
    }

    @PostMapping("/{clientId}/delete")
    public ResponseEntity<RestApiResponse> deleteClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(clientService.deleteClient(clientId));
    }
}
