package com.project_management.project_management.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "status")
@Getter
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String status_id;
    @Column(unique = true)
    private String status_name;
    private String status_color;
    private String status_bg;

    @OneToOne
    private Task task;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Status status = (Status) o;
        return Objects.equals(status_id, status.status_id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(status_id);
    }
}
