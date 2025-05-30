package com.ecole.management.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sequence_counter")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SequenceCounter {

    @Id
    private String name;

    @Column(nullable = false)
    private Long currentValue;

    public SequenceCounter(String name) {
        this.name = name;
        this.currentValue = 0L;
    }

    public Long getNextValue() {
        return ++currentValue;
    }
}
