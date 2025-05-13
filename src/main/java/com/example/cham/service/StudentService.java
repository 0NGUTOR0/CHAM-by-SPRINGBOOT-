package com.example.cham.service;

import com.example.cham.model.Student;
import com.example.cham.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    // Create a new student
    public Student createStudent(Student student) {
        return studentRepository.save(student);
    }

    // Get all students
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    // Get a student by ID
    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    // Update student details
    public Student updateStudent(Long id, Student studentDetails) {
        return studentRepository.findById(id).map(student -> {
            student.setName(studentDetails.getName());
            student.setGrade(studentDetails.getGrade());
            student.setAge(studentDetails.getAge());
            student.setLanguage(studentDetails.getLanguage());
            student.setParentName(studentDetails.getParentName());
            student.setEmail(studentDetails.getEmail());
            return studentRepository.save(student);
        }).orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));
    }

    // Delete a student by ID
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}
