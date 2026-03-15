package com.project_management.project_management.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tags")
@Getter
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String tag_id;
    @Column(unique = true)
    private String tag_name;
    private String tag_color;
    private String tag_bg;

    @ManyToMany(mappedBy = "task_tags")
    private Set<Task> tasks;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(tag_id, tag.tag_id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tag_id);
    }
}
