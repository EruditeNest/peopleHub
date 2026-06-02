package com.people.hub.taskmanager.service;

import com.people.hub.common.RestApiResponse;
import com.people.hub.common.exception.BadRequestException;
import com.people.hub.common.exception.NotFoundException;
import com.people.hub.common.utilities.PageableUtils;
import com.people.hub.taskmanager.dto.ClientDto;
import com.people.hub.taskmanager.model.Client;
import com.people.hub.taskmanager.repo.ClientRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ClientService {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id",
            "name",
            "email",
            "contact",
            "created_at",
            "updated_at"
    );

    private final ClientRepo clientRepo;

    public Client createClient(ClientDto dto) {

        if (clientRepo.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        if (clientRepo.existsByContact(dto.getContact())) {
            throw new BadRequestException("Contact already exists");
        }

        Client client = new Client();

        client.setName(dto.getName());
        client.setDescription(dto.getDescription());
        client.setAddress(dto.getAddress());
        client.setEmail(dto.getEmail());
        client.setContact(dto.getContact());

        return clientRepo.save(client);
    }

    public Client updateClient(
            Long clientId,
            ClientDto dto) {
        Client client = getClientById(clientId);
        if (clientRepo.existsByEmailAndIdNot(dto.getEmail(), clientId)) {
            throw new BadRequestException("Email already exists");
        }
        if (clientRepo.existsByContactAndIdNot(dto.getContact(), clientId)) {
            throw new BadRequestException("Contact already exists");
        }
        client.setName(dto.getName());
        client.setDescription(dto.getDescription());
        client.setAddress(dto.getAddress());
        client.setEmail(dto.getEmail());
        client.setContact(dto.getContact());
        return clientRepo.save(client);
    }

    public Client getClientById(Long clientId) {
        return clientRepo.findById(clientId)
                .orElseThrow(() -> new NotFoundException("Client not found", clientId));
    }

    public RestApiResponse getClients(
            int page,
            int size,
            String sortField,
            String sortOrder) {
        Pageable pageable = PageableUtils.getPageable(page, size, sortField, sortOrder, ALLOWED_SORT_FIELDS);
        Page<Client> clients = clientRepo.findAll(pageable);
        return RestApiResponse.success(clients);
    }

    public RestApiResponse deleteClient(Long clientId) {
        clientRepo.deleteById(clientId);
        return RestApiResponse.success("Client deleted successfully");
    }
}
