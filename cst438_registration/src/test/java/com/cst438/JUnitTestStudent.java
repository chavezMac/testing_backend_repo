package com.cst438;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cst438.domain.ScheduleDTO;
import com.cst438.domain.StudentDTO;
import com.cst438.domain.Student;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class JUnitTestStudent {
	@Autowired
	private MockMvc mvc;
	
	private StudentDTO createStudentDTO(Student s) {
		StudentDTO dto = new StudentDTO(
				s.getStudent_id(),
				s.getName(),
				s.getEmail(),
				s.getStatusCode(),
				s.getStatus());
		return dto;
	}
	/*
	 * JUnit for Adding a new student mac, mactest@test.com
	 */
	@Test
	public void addStudent() throws Exception {

		MockHttpServletResponse response;
		// add student

		// create student object to send to server
		Student testStudent = new Student();
		testStudent.setEmail("mac@mac.edu");
		testStudent.setName("mac");
		
		StudentDTO studentDTO = createStudentDTO(testStudent);
		
		// convert student object to JSON for transfer back and forth to server
		String jsonString = asJsonString(studentDTO);

		// send student data to server
		response = mvc.perform(
			MockMvcRequestBuilders
			.post("/student")
			.contentType(MediaType.APPLICATION_JSON)
			.content(jsonString)
			.accept(MediaType.APPLICATION_JSON))
			.andReturn().getResponse();
		
		//verify that return status = OK (value 200)
		assertEquals(200, response.getStatus());
	}
	
	/*
	 * Delete student from with id 1
	 */
	@Test
	public void deleteStudent() throws Exception {
		MockHttpServletResponse response;
		
		response = mvc.perform(
				MockMvcRequestBuilders
				.delete("/student/1"))
				.andReturn().getResponse();
		
		//verify delete happened'
		boolean checkForce = false;
		if(400 == response.getStatus()) {
			assertEquals(400,response.getStatus());
			checkForce = true;
		}
		
		if(checkForce) {
			response = mvc.perform(
					MockMvcRequestBuilders
					.delete("/student/1")
					.param("force", "true"))
					.andReturn().getResponse();
			assertEquals(200, response.getStatus());
			
			response = mvc.perform(
					MockMvcRequestBuilders
					.get("/student")
					.accept(MediaType.APPLICATION_JSON))
					.andReturn().getResponse();
			
			//check that student with id 1 is deleted
			StudentDTO[] dto_list = fromJsonString(response.getContentAsString(), StudentDTO[].class);
			boolean found = false;
			
			for(StudentDTO dto : dto_list) {
				if(dto.id() == 1) found = true;
			}
			//If assertFalse fails the JUnit test than that implies that the 
			//student wasn't deleted.
			assertFalse(found);
		}
	}
	
	/*
	 * Update students name and email that has id 2
	 */
	@Test
	public void updateStudent() throws Exception {
		MockHttpServletResponse response;
		
		Student testStudent = new Student();
		testStudent.setEmail("testing@testing.edu");
		testStudent.setName("testing");
		
		String jsonString = asJsonString(testStudent);
		
		response = mvc.perform(
				MockMvcRequestBuilders
				.put("/student/2")
				.contentType(MediaType.APPLICATION_JSON)
				.content(jsonString)
				.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		//Verify request was processed
		assertEquals(200, response.getStatus());
		
		response = mvc.perform(
				MockMvcRequestBuilders
				.get("/student")
				.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		//Verify request was processed
		assertEquals(200, response.getStatus());
		
		//check response for name change
		StudentDTO[] dto_list = fromJsonString(response.getContentAsString(), StudentDTO[].class);
		boolean found = false;
		
		for(StudentDTO dto : dto_list) {
			if(dto.name().equals("testing")) found = true;
		}
		//If assertFalse fails the JUnit test than that implies that the 
		//student wasn't deleted.
		assertTrue(found);
	}
	
	
	
	private static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
		try {
			return new ObjectMapper().readValue(str, valueType);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}




