package com.min.holidaykeeper.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "holiday", indexes = {@Index(name = "idx_holidays_year_country", columnList = "holiday_year, country_code")})
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Holiday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "holiday_id")
    private Long id;

    @NotNull
    private LocalDate date;

    @NotNull
    private String localName;

    @NotNull
    private String name;

    @NotNull
    @Column(name = "country_code")
    private String countryCode;

    @NotNull
    private boolean fixed;

    @NotNull
    private boolean global;

    private String counties;

    private Integer launchYear;

    @ElementCollection
    @CollectionTable(name = "holiday_types", joinColumns = @JoinColumn(name = "holiday_id"))
    @Column(name = "type")
    private Set<String> types = new HashSet<>();

    private int holidayYear;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code", insertable = false, updatable = false)
    private Country country;
}
