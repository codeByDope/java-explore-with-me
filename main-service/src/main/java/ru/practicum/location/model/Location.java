package ru.practicum.location.model;

import lombok.*;

import javax.persistence.*;

@ToString
@Getter
@Setter
@Builder
@Entity
@Table(name = "locations")
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Float lat;

    private Float lon;
}
