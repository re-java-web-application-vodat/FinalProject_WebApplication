package com.smartlab.repository;

import com.smartlab.entity.Lecturer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Long> {

    List<Lecturer> findByDepartmentId(Long departmentId);

    Optional<Lecturer> findByUser_Username(String username);

    @Query("SELECT l FROM Lecturer l JOIN FETCH l.department JOIN FETCH l.user ORDER BY l.id")
    List<Lecturer> findAllWithDetails();
}