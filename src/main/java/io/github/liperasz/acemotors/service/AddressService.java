package io.github.liperasz.acemotors.service;

import io.github.liperasz.acemotors.dto.address.AddressRequest;
import io.github.liperasz.acemotors.entity.Address;
import io.github.liperasz.acemotors.repository.AddressRepository;
import io.github.liperasz.acemotors.repository.OrderRepository;
import io.github.liperasz.acemotors.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Endereços sempre acessados a partir do cliente: /clients/{clientId}/addresses
@Service
public class AddressService {

    private final AddressRepository addressRepository;

    private final OrderRepository orderRepository;

    private final ClientService clientService;

    public AddressService (AddressRepository addressRepository, OrderRepository orderRepository,
                           ClientService clientService) {
        this.addressRepository = addressRepository;
        this.orderRepository = orderRepository;
        this.clientService = clientService;
    }

    public Page<Address> findAll(Long clientId, Pageable pageable) {
        clientService.findById(clientId);
        return addressRepository.findByPersonIdAndActiveTrue(clientId, pageable);
    }

    // Só encontra se o endereço for desse cliente e estiver ativo
    public Address findById(Long clientId, Long id) {
        return addressRepository.findByIdAndPersonIdAndActiveTrue(id, clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço", id));
    }

    @Transactional
    public Address create(Long clientId, AddressRequest request) {
        Address address = new Address();
        address.setPerson(clientService.findById(clientId));
        copyToEntity(request, address);
        return addressRepository.save(address);
    }

    @Transactional
    public Address update(Long clientId, Long id, AddressRequest request) {
        Address address = findById(clientId, id);
        copyToEntity(request, address);
        return addressRepository.save(address);
    }

    // Endereço usado em pedido só é desativado (o pedido continua apontando para ele)
    @Transactional
    public void delete(Long clientId, Long id) {
        Address address = findById(clientId, id);

        if (orderRepository.existsByAddressId(id)) {
            address.setActive(false);
            addressRepository.save(address);
        } else {
            addressRepository.delete(address);
        }
    }

    private void copyToEntity(AddressRequest request, Address address) {
        address.setStreet(request.street());
        address.setNumber(request.number());
        address.setComplement(request.complement());
        address.setNeighborhood(request.neighborhood());
        address.setCity(request.city());
        address.setState(request.state());
        address.setZipCode(request.zipCode());
    }
}
