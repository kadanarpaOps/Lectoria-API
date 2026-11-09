package com.lectoria_api.stories.infrastructure.output.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "T_GENRES")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenreEntity {
    @Id
    @Column(name = "GENRE_ID")
    private UUID id;

    @Column(name = "GENRE_NAME", nullable = false, unique = true, length = 50)
    private String name;
}
