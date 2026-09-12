package com.al_lado.al_lado_project.repository.trabajo;

import com.al_lado.al_lado_project.entity.trabajo.PostBase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostBaseRepository extends JpaRepository<PostBase, Long> {

    List<PostBase> findByAutorId(Long autorId);
}