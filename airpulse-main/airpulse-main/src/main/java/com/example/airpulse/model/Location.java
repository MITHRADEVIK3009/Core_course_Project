package com.example.airpulse.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "locations")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LocationType type;

    @Column(name = "code_prefix", nullable = false, length = 2)
    private String codePrefix;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "region_id", nullable = false)
    @JsonBackReference
    private Region region;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonManagedReference
    private List<Sensor> sensors = new ArrayList<>();

    @PrePersist
    @PreUpdate
    private void setCodePrefixFromType() {
        if (type != null) {
            this.codePrefix = type.getCodePrefix();
        }
    }
} 