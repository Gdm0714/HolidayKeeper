package com.min.holidaykeeper.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Entity
@Table
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Country {
    @Id
    @Column(name = "country_code")
    @Length(max = 2)
    private String countryCode;

    @NotNull
    @Length(max = 100)
    private String name;
}
