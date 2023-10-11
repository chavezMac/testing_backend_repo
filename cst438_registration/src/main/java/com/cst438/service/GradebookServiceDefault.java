package com.cst438.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(prefix="gradebook", name="service", havingValue = "default", matchIfMissing=true)
public class GradebookServiceDefault implements GradebookService {
	
	public void enrollStudent(String student_email, String student_name, int course_id) {
	}
	
}
