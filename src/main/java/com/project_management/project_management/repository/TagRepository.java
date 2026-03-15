package com.project_management.project_management.repository;

import com.project_management.project_management.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {
    @Query("SELECT tag FROM Tag tag WHERE tag.tag_name = :name")
    public Tag findByTagName(@Param("name") String name);
}
