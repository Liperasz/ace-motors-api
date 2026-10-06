package io.github.liperasz.acemotors.entity;

import jakarta.persistence.*;

import java.util.Set;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToMany(mappedBy = "categories")
    private Set<Product> products;
}
