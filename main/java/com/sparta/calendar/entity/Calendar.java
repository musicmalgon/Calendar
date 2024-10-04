package com.sparta.calendar.entity;

import com.sparta.calendar.dto.CalendarRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Calendar {
    private Long id;
    private String task;
    private String username;
    private String password;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Calendar(CalendarRequestDto requestDto) {
        this.task = task;
        this.username = username;
        this.password = password;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void update(CalendarRequestDto requestDto) {
        this.task = requestDto.getTask();
        this.username = requestDto.getUsername();
        this.password = requestDto.getPassword();
    }
}