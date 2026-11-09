package io.github.liperasz.acemotors.service;

import io.github.liperasz.acemotors.dto.administrator.AdministratorCreateRequest;
import io.github.liperasz.acemotors.dto.administrator.AdministratorUpdateRequest;
import io.github.liperasz.acemotors.entity.Administrator;
import io.github.liperasz.acemotors.repository.AdministratorRepository;
import io.github.liperasz.acemotors.service.exception.BusinessException;
import io.github.liperasz.acemotors.service.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdministratorService {

    private final AdministratorRepository administratorRepository;

    private final PersonService personService;

    public AdministratorService (AdministratorRepository administratorRepository, PersonService personService) {
        this.administratorRepository = administratorRepository;
        this.personService = personService;
    }

    public Page<Administrator> findAll(Pageable pageable) {
        return administratorRepository.findAll(pageable);
    }

    public Administrator findById(Long id) {
        return administratorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administrador", id));
    }

    // Usado quando o adminId vem no corpo de outra requisição (ex: mudança de status).
    // Por isso é 400 (dado inválido no pedido) e não 404
    public Administrator findForAction(Long adminId) {
        return administratorRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException("administrator.not-found",
                        "Administrador com id " + adminId + " não existe"));
    }

    @Transactional
    public Administrator create(AdministratorCreateRequest request) {
        personService.validateUniqueEmail(request.email(), null);

        Administrator admin = new Administrator();
        admin.setName(request.name());
        admin.setEmail(request.email());
        // TODO checkpoint 3: salvar a senha com hash (BCrypt)
        admin.setPasswordHash(request.password());

        return administratorRepository.save(admin);
    }

    @Transactional
    public Administrator update(Long id, AdministratorUpdateRequest request) {
        Administrator admin = findById(id);
        personService.validateUniqueEmail(request.email(), id);

        admin.setName(request.name());
        admin.setEmail(request.email());

        return administratorRepository.save(admin);
    }

    // Administrador que já alterou algum status não pode ser excluído (o banco barra -> 400)
    @Transactional
    public void delete(Long id) {
        Administrator admin = findById(id);
        administratorRepository.delete(admin);
        administratorRepository.flush();
    }
}
