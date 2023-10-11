package com.cst438.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentRepository;
import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.Student;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.StudentRepository;
import com.cst438.service.GradebookService;


@RestController
@CrossOrigin
public class StudentController {
	
	@Autowired
	StudentRepository studentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	GradebookService gradebookService;

    /*
	 * list all students
	 */
	@GetMapping("/student")
	public StudentDTO[] getAllStudents() {
		System.out.println("/student called.");
		List<Student> students = (List<Student>) studentRepository.findAll();
		Student stud = null;
		
		if(students.isEmpty()) {
			return new StudentDTO[0];
		}else {
			StudentDTO[] dto = new StudentDTO[students.size()];
			
			for(int i = 0;i < students.size(); i++) {
				stud = students.get(i);
				dto[i] = createStudentDTO(stud);
			}
			return dto;
		}
	}
        
    @GetMapping("/student/{id}")
    public StudentDTO getStudent(@PathVariable("id") int id) {
        // return student data given a student id 
        // return HTTP status 404 if not found

        Optional<Student> student = studentRepository.findById(id);
        StudentDTO[] dto = new StudentDTO[1];
        
        if(student.isPresent()) {
        	dto[0] = createStudentDTO(student.get());
        	return dto[0];
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
    }  
        
    @PutMapping("/student/{id}") 
    public void updateStudent(@PathVariable("id")int id, @RequestBody StudentDTO sdto) {
        // update/replace student with data in StudentDTO

        Optional<Student> student = studentRepository.findById(id);
        Student emailCheck = studentRepository.findByEmail(sdto.email());
        		
        if(student.isPresent() && emailCheck == null) {
            Student s = student.get();
            s.setEmail(sdto.email());
            s.setName(sdto.name());
            s.setStatusCode(sdto.statusCode());
            s.setStatus(sdto.status());
            studentRepository.save(s);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
    }
    
    @PostMapping("/student")
    public int createStudent(@RequestBody StudentDTO sdto) {
        //  create new student with data in StudentDTO. 
        //  return the new student id

        Student s = new Student();
        s.setEmail(sdto.email());
        Student temp = studentRepository.findByEmail(sdto.email());
        
        if(temp != null) {
        	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student email already exists");
        }else {
        	s.setName(sdto.name());
        	studentRepository.save(s);
        }
        return s.getStudent_id();

    }
        
    @DeleteMapping("/student/{id}")
    public void deleteStudent(@PathVariable("id") int id, 
                            @RequestParam("force") Optional<String> force) {
        // delete a student 
        // if student has enrollments, 
        // then delete the student only if "force" parameter is specified

        Optional<Student> student = studentRepository.findById(id);
        if(student.isPresent()) {
            Student s = student.get();
            Enrollment enrollments = enrollmentRepository.findById(id).orElse(null);
            if(enrollments == null || force.isPresent()) {
                studentRepository.delete(s);
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student has enrollments");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
    }

	private StudentDTO createStudentDTO(Student s) {
		StudentDTO dto = new StudentDTO(
				s.getStudent_id(),
				s.getName(),
				s.getEmail(),
				s.getStatusCode(),
				s.getStatus());
		return dto;
	}

}