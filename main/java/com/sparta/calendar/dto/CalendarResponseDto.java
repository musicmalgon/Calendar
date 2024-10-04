package com.sparta.calendar.dto;

import com.sparta.calendar.entity.Calendar;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class CalendarResponseDto {
    private Long calendarId;
    private String task;
    private String username;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CalendarResponseDto(Calendar calendar) {
        this.calendarId = calendar.getId();
        this.task = calendar.getTask();
        this.username = calendar.getUsername();
        this.createdAt = calendar.getCreatedAt();
        this.updatedAt = calendar.getUpdatedAt();
    }

    public CalendarResponseDto(Long calendarId, String task, String username, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.calendarId = calendarId;
        this.task = task;
        this.username = username;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}