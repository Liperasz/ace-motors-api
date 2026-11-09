package io.github.liperasz.acemotors.repository.client;

import io.github.liperasz.acemotors.entity.Client;
import io.github.liperasz.acemotors.repository.filter.ClientFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientRepositoryQuery {

    Page<Client> filter(ClientFilter filter, Pageable pageable);
}
