package com.al_lado.al_lado_project.repository;

import com.al_lado.al_lado_project.entity.trabajo.PostBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostBaseRepository extends JpaRepository<PostBase, Long> {
}