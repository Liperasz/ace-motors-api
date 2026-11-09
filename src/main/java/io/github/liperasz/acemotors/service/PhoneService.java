package io.github.liperasz.acemotors.service;

import io.github.liperasz.acemotors.dto.phone.PhoneRequest;
import io.github.liperasz.acemotors.entity.Phone;
import io.github.liperasz.acemotors.repository.PhoneRepository;
import io.github.liperasz.acemotors.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Telefones sempre acessados a partir do cliente: /clients/{clientId}/phones
@Service
public class PhoneService {

    private final PhoneRepository phoneRepository;

    private final ClientService clientService;

    public PhoneService (PhoneRepository phoneRepository, ClientService clientService) {
        this.phoneRepository = phoneRepository;
        this.clientService = clientService;
    }

    public Page<Phone> findAll(Long clientId, Pageable pageable) {
        clientService.findById(clientId);
        return phoneRepository.findByPersonId(clientId, pageable);
    }

    public Phone findById(Long clientId, Long id) {
        return phoneRepository.findByIdAndPersonId(id, clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Telefone", id));
    }

    @Transactional
    public Phone create(Long clientId, PhoneRequest request) {
        Phone phone = new Phone();
        phone.setPerson(clientService.findById(clientId));
        phone.setNumber(request.number());
        return phoneRepository.save(phone);
    }

    @Transactional
    public Phone update(Long clientId, Long id, PhoneRequest request) {
        Phone phone = findById(clientId, id);
        phone.setNumber(request.number());
        return phoneRepository.save(phone);
    }

    @Transactional
    public void delete(Long clientId, Long id) {
        phoneRepository.delete(findById(clientId, id));
    }
}
