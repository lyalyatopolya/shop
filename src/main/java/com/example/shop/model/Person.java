package com.example.shop.model;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.domain.AbstractPersistable;

import java.time.LocalDate;
import java.util.UUID;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public @NotNull(message = "Необходимо указать дату рождения") LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(@NotNull(message = "Необходимо указать дату рождения") LocalDate birthDate) {
        this.birthDate = birthDate;
    }
}
