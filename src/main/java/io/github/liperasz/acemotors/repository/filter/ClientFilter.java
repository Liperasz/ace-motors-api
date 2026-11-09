package io.github.liperasz.acemotors.repository.filter;

import io.github.liperasz.acemotors.enums.ClientStatus;

// Parâmetros opcionais da pesquisa de clientes (/clients?name=&email=&status=)
public class ClientFilter {

    private String name;

    private String email;

    private ClientStatus status;

    public ClientFilter () {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ClientStatus getStatus() {
        return status;
    }

    public void setStatus(ClientStatus status) {
        this.status = status;
    }
}
