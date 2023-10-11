package com.cst438.domain;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository <Student, Integer> {
	
	 Student findByEmail(String email); 
	
	 Student[] findByNameStartsWith(String name);
	
}
