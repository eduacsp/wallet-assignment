package com.eduacsp.walletassignment.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "wallet_user")
public class UserEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "cpf_cnpj", nullable = false, unique = true, length = 20)
    private String cpfCnpj;

    public UserEntity() {
    }

    public UserEntity(UUID id, String name, String cpfCnpj) {
        this.id = id;
        this.name = name;
        this.cpfCnpj = cpfCnpj;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }
}
