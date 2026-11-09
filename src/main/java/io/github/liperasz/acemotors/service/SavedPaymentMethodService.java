package io.github.liperasz.acemotors.service;

import io.github.liperasz.acemotors.dto.paymentmethod.SavedPaymentMethodRequest;
import io.github.liperasz.acemotors.dto.paymentmethod.SavedPaymentMethodUpdateRequest;
import io.github.liperasz.acemotors.entity.SavedPaymentMethod;
import io.github.liperasz.acemotors.enums.PaymentMethod;
import io.github.liperasz.acemotors.repository.SavedPaymentMethodRepository;
import io.github.liperasz.acemotors.service.exception.BusinessException;
import io.github.liperasz.acemotors.service.exception.ResourceNotFoundException;
import io.github.liperasz.acemotors.util.CardUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Formas de pagamento salvas do cliente: só a versão mascarada é guardada
@Service
public class SavedPaymentMethodService {

    private final SavedPaymentMethodRepository repository;

    private final ClientService clientService;

    public SavedPaymentMethodService (SavedPaymentMethodRepository repository, ClientService clientService) {
        this.repository = repository;
        this.clientService = clientService;
    }

    public Page<SavedPaymentMethod> findAll(Long clientId, Pageable pageable) {
        clientService.findById(clientId);
        return repository.findByClientId(clientId, pageable);
    }

    public SavedPaymentMethod findById(Long clientId, Long id) {
        return repository.findByIdAndClientId(id, clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Forma de pagamento", id));
    }

    @Transactional
    public SavedPaymentMethod create(Long clientId, SavedPaymentMethodRequest request) {
        SavedPaymentMethod method = new SavedPaymentMethod();
        method.setClient(clientService.findActive(clientId));
        method.setType(request.type());
        method.setNickname(request.nickname());
        method.setMaskedData(mask(request));
        return repository.save(method);
    }

    @Transactional
    public SavedPaymentMethod update(Long clientId, Long id, SavedPaymentMethodUpdateRequest request) {
        SavedPaymentMethod method = findById(clientId, id);
        method.setNickname(request.nickname());
        return repository.save(method);
    }

    // Forma de pagamento já usada em algum pagamento não pode ser excluída (o banco barra -> 400)
    @Transactional
    public void delete(Long clientId, Long id) {
        repository.delete(findById(clientId, id));
        repository.flush();
    }

    // Cartão precisa ter número válido; PIX guarda a chave mascarada
    private String mask(SavedPaymentMethodRequest request) {
        if (request.type() == PaymentMethod.PIX) {
            return CardUtils.maskPixKey(request.data().trim());
        }

        if (!CardUtils.isValidCardNumber(request.data())) {
            throw new BusinessException("card.invalid", "Número de cartão inválido");
        }
        return CardUtils.maskCard(request.data());
    }
}
