package com.example.cham.controller;

import com.example.cham.dto.StudentRequestDTO;
import com.example.cham.model.Note;
import com.example.cham.model.Student;
import com.example.cham.model.User;
import com.example.cham.repository.StudentRepository;
import com.example.cham.repository.UserRepository;
import com.example.cham.service.AuthServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/cham/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthServiceImpl authService; // Handles extracting userId from JWT

    // Get all students for the logged-in teacher
    @GetMapping
    public ResponseEntity<List<Student>> getStudentsByTeacher(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int limit,
            @RequestHeader("Authorization") String authorizationHeader) {

        try {
            // Validate and extract token
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            String token = authorizationHeader.substring(7); // Remove "Bearer " prefix

            // Extract teacher ID from token
            Long userId;
            try {
                userId = Long.parseLong(authService.getUserIdFromToken(token));
            } catch (NumberFormatException | NullPointerException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // Validate pagination parameters
            if (page < 1 || limit < 1) {
                return ResponseEntity.badRequest().body(null);
            }

            // Create pageable object
            Pageable pageable = PageRequest.of(page - 1, Math.min(limit, 100)); // Cap limit to 100

            // Fetch students for the given teacher ID
            List<Student> studentsPage = studentRepository.findByTeacherId(userId, pageable);

            // Return paginated response
            return ResponseEntity.ok(studentsPage);

        } catch (Exception e) {
            // Use System.err.println instead of logger
            System.err.println("Error fetching students for teacher ID: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        }


    // Add a student
    @PostMapping("/addlearner")
    public ResponseEntity<Student> addLearner(@RequestBody StudentRequestDTO studentDTO,
                                              @RequestHeader("Authorization") String token) {
        Long userId = Long.parseLong(authService.getUserIdFromToken(token));
        Optional<User> teacher = userRepository.findById(userId);
    
        if (teacher.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    
        int currentYear = java.time.Year.now().getValue();
        
        int age = currentYear - studentDTO.birthYear;
        System.out.println(studentDTO.birthYear);
        Student student = new Student(
            studentDTO.name,
            studentDTO.grade,
            age,
            studentDTO.language,
            studentDTO.parentName,
            studentDTO.email,
            teacher.get()
        );
    
        try {
            Student savedStudent = studentRepository.save(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A parent with that email already exists.");
        }
    }
    


    // Add a note to a student
  @PostMapping("/notes/{studentID}")
public ResponseEntity<?> addNote(@PathVariable String studentID,
                                 @RequestBody String noteContent,
                                 @RequestHeader("Authorization") String token) {
    Long teacherId = Long.parseLong(authService.getUserIdFromToken(token));
    Long studentId = Long.parseLong(studentID);

    Optional<Student> optionalStudent = studentRepository.findByIdAndTeacher_Id(studentId, teacherId);

    if (optionalStudent.isEmpty()) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Student not found or unauthorized");
    }

    Student student = optionalStudent.get();

    // ✅ Convert string to Note object
    Note newNote = new Note(noteContent);
    student.getNotes().add(newNote);

    studentRepository.save(student);
    return ResponseEntity.status(HttpStatus.CREATED).body("Note successfully added");
}

    // Get notes for a student
    @GetMapping("/notes/{studentID}")
    public ResponseEntity<?> getNotes(@PathVariable String studentID,
                                      @RequestHeader("Authorization") String token) {
        Long teacherId = Long.parseLong(authService.getUserIdFromToken(token));
        Long studentId = Long.parseLong(studentID);
    
        Optional<Student> optionalStudent = studentRepository.findByIdAndTeacher_Id(studentId, teacherId);
    
        if (optionalStudent.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Student not found or unauthorized");
        }
    
        return ResponseEntity.ok(optionalStudent.get().getNotes());
    }
    
    // Update student details
    @PatchMapping("/{studentID}")
    public ResponseEntity<?> updateStudent(@PathVariable Long studentID,
                                           @RequestBody Student updatedStudent) {
        Optional<Student> optionalStudent = studentRepository.findById(studentID);

        if (!optionalStudent.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }

        Student student = optionalStudent.get();
        student.setName(updatedStudent.getName());
        student.setEmail(updatedStudent.getEmail());
        studentRepository.save(student);
        return ResponseEntity.ok(student);
    }

    // Delete student
    @DeleteMapping("/{studentID}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long studentID) {
        Optional<Student> optionalStudent = studentRepository.findById(studentID);

        if (!optionalStudent.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
        }

        studentRepository.deleteById(studentID);
        return ResponseEntity.ok("Student deleted successfully");
    }
}
