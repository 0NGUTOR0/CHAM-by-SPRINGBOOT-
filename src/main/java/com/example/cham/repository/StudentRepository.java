package com.example.cham.repository;

import com.example.cham.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;



@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Page<Student> findAll(Pageable pageable);
    // Custom query method to find students by teacher ID
    List<Student> findByTeacherId(Long teacherId, Pageable pageable);

    Optional<Student> findByIdAndTeacher_Id(Long studentId, Long teacherId);

    Optional<Student> findById(Long studentID);



}
