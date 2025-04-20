package com.example.shop.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "shop_Person")
@Table(name = "SHOP_PERSON")
public class Person extends AbstractPersistable<UUID> {

    @NotEmpty
    @Column(name = "name_", nullable = false)
    private String name;

    @NotNull(message = "Необходимо указать дату рождения")
    @Column(name = "BIRTH_DATE", nullable = false)
    @Temporal(TemporalType.DATE)
    private LocalDate birthDate;
}
