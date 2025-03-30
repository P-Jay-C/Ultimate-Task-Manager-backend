package org.jay.taskmanager.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String color; // Optional: Hex code (e.g., "#FF0000" for red)

    @ManyToMany(mappedBy = "tags")
    private Set<Task> tasks = new HashSet<>();
}