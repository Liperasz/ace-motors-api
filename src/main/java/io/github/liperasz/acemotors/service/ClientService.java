package io.github.liperasz.acemotors.service;

import io.github.liperasz.acemotors.dto.client.ClientCreateRequest;
import io.github.liperasz.acemotors.dto.client.ClientStatusRequest;
import io.github.liperasz.acemotors.dto.client.ClientUpdateRequest;
import io.github.liperasz.acemotors.entity.Client;
import io.github.liperasz.acemotors.entity.ClientStatusHistory;
import io.github.liperasz.acemotors.enums.ClientStatus;
import io.github.liperasz.acemotors.repository.*;
import io.github.liperasz.acemotors.repository.filter.ClientFilter;
import io.github.liperasz.acemotors.service.exception.BusinessException;
import io.github.liperasz.acemotors.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    private final ClientStatusHistoryRepository historyRepository;

    private final AddressRepository addressRepository;

    private final PhoneRepository phoneRepository;

    private final FavoriteRepository favoriteRepository;

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final SavedPaymentMethodRepository savedPaymentMethodRepository;

    private final PersonService personService;

    private final AdministratorService administratorService;

    public ClientService (ClientRepository clientRepository, ClientStatusHistoryRepository historyRepository,
                          AddressRepository addressRepository, PhoneRepository phoneRepository,
                          FavoriteRepository favoriteRepository, CartRepository cartRepository,
                          CartItemRepository cartItemRepository,
                          SavedPaymentMethodRepository savedPaymentMethodRepository,
                          PersonService personService, AdministratorService administratorService) {
        this.clientRepository = clientRepository;
        this.historyRepository = historyRepository;
        this.addressRepository = addressRepository;
        this.phoneRepository = phoneRepository;
        this.favoriteRepository = favoriteRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.savedPaymentMethodRepository = savedPaymentMethodRepository;
        this.personService = personService;
        this.administratorService = administratorService;
    }

    public Page<Client> filter(ClientFilter filter, Pageable pageable) {
        return clientRepository.filter(filter, pageable);
    }

    public Client findById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }

    // Regra de negócio: só cliente ativo pode comprar, favoritar, avaliar etc.
    public Client findActive(Long id) {
        Client client = findById(id);

        if (client.getStatus() != ClientStatus.ACTIVE) {
            throw new BusinessException("client.inactive", "Cliente " + id + " está inativo");
        }

        return client;
    }

    @Transactional
    public Client create(ClientCreateRequest request) {
        personService.validateUniqueEmail(request.email(), null);

        Client client = new Client();
        client.setName(request.name());
        client.setEmail(request.email());
        // TODO checkpoint 3: salvar a senha com hash (BCrypt)
        client.setPasswordHash(request.password());

        return clientRepository.save(client);
    }

    @Transactional
    public Client update(Long id, ClientUpdateRequest request) {
        Client client = findById(id);
        personService.validateUniqueEmail(request.email(), id);

        client.setName(request.name());
        client.setEmail(request.email());

        return clientRepository.save(client);
    }

    // Apaga junto telefones, endereços, favoritos, carrinho e formas de pagamento.
    // Se o cliente já tiver pedidos ou histórico, o banco barra (400) e nada é apagado
    @Transactional
    public void delete(Long id) {
        Client client = findById(id);

        favoriteRepository.deleteByClientId(id);
        cartItemRepository.deleteByCartClientId(id);
        cartRepository.deleteByClientId(id);
        savedPaymentMethodRepository.deleteByClientId(id);
        phoneRepository.deleteByPersonId(id);
        addressRepository.deleteByPersonId(id);
        clientRepository.delete(client);
        clientRepository.flush();
    }

    // Atualização parcial: ativa/inativa o cliente e registra no histórico
    @Transactional
    public Client changeStatus(Long id, ClientStatusRequest request) {
        Client client = findById(id);

        if (client.getStatus() == request.status()) {
            throw new BusinessException("status.unchanged", "O cliente já está com status " + request.status());
        }

        ClientStatusHistory history = new ClientStatusHistory();
        history.setClient(client);
        history.setChangedBy(administratorService.findForAction(request.adminId()));
        history.setPreviousStatus(client.getStatus());
        history.setNewStatus(request.status());
        history.setUpdateNotes(request.notes());
        historyRepository.save(history);

        client.setStatus(request.status());
        return clientRepository.save(client);
    }

    public Page<ClientStatusHistory> findStatusHistory(Long id, Pageable pageable) {
        findById(id);
        return historyRepository.findByClientIdOrderByUpdateTimeDesc(id, pageable);
    }
}
