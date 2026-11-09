package io.github.liperasz.acemotors.service;

import io.github.liperasz.acemotors.repository.PersonRepository;
import io.github.liperasz.acemotors.service.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

// Regras comuns a cliente e administrador
@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService (PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    // O e-mail é o login, então não pode repetir entre nenhuma pessoa do sistema.
    // personId = null no cadastro; no update é o id de quem está sendo alterado
    public void validateUniqueEmail(String email, Long personId) {
        boolean inUse = personId == null
                ? personRepository.existsByEmail(email)
                : personRepository.existsByEmailAndIdNot(email, personId);

        if (inUse) {
            throw new BusinessException("email.already-in-use", "E-mail " + email + " já cadastrado",
                    HttpStatus.CONFLICT);
        }
    }
}
