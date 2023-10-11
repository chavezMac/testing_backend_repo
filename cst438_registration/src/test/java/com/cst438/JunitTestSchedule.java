package com.cst438;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cst438.domain.ScheduleDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

/* 
 * Example of using Junit 
 * Mockmvc is used to test a simulated REST call to the RestController
 */
@SpringBootTest
@AutoConfigureMockMvc
public class JunitTestSchedule {

	@Autowired
	private MockMvc mvc;

	/*
	 * add course 40442 to student test@csumb.edu in schedule Fall 2021
	 */
	@Test
	public void addCourse()  throws Exception {
		
		MockHttpServletResponse response;

		response = mvc.perform(
				MockMvcRequestBuilders
			      .post("/schedule/course/40442")
			      .contentType(MediaType.APPLICATION_JSON)
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
		
		// verify that returned data has non zero primary key
		ScheduleDTO result = fromJsonString(response.getContentAsString(), ScheduleDTO.class);
		assertNotEquals( 0  , result.id());
		
		
		// do http GET for student schedule 
		response = mvc.perform(
				MockMvcRequestBuilders
			      .get("/schedule?year=2021&semester=Fall")
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
		
		// verify that returned data contains the added course 
		ScheduleDTO[] dto_list = fromJsonString(response.getContentAsString(), ScheduleDTO[].class);
		
		boolean found = false;		
		for (ScheduleDTO sc : dto_list) {
			if (sc.courseId() == 40442) {
				found = true;
			}
		}
		assertEquals(true, found, "Added course not in updated schedule.");
		
	}
	/*
	 * drop course 30157 Fall 2020 from student test@csumb.edu
	 */
	@Test
	public void dropCourse()  throws Exception {
		
		MockHttpServletResponse response;
		
		response = mvc.perform(
				MockMvcRequestBuilders
			      .get("/schedule?year=2020&semester=Fall")
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		// verify that 30157 is in student schedule
		ScheduleDTO[] dto_list = fromJsonString(response.getContentAsString(), ScheduleDTO[].class);
		boolean found = false;
		for (ScheduleDTO dto : dto_list) {
			if (dto.courseId()==30157) found=true;
		}
		assertTrue(found);

		// drop course 30157 in Fall 2020
		response = mvc.perform(
				MockMvcRequestBuilders
			      .delete("/schedule/1"))
				.andReturn().getResponse();
		
		// verify that return status = OK (value 200) 
		assertEquals(200, response.getStatus());
	
		response = mvc.perform(
				MockMvcRequestBuilders
			      .get("/schedule?year=2020&semester=Fall")
			      .accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		// verify that 30157 is not in student schedule
		dto_list = fromJsonString(response.getContentAsString(), ScheduleDTO[].class);
		found = false;
		for (ScheduleDTO dto : dto_list) {
			if (dto.courseId()==30157) found=true;
		}
		assertFalse(found);
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
