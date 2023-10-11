package com.cst438.domain;

public record ScheduleDTO 
  (int id, int courseId, int section, String title, String times, String building, String room, String instructor, String startDate, String endDate, String grade) {

}
