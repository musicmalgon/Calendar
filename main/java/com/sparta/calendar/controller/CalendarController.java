package com.sparta.calendar.controller;

import com.sparta.calendar.dto.CalendarRequestDto;
import com.sparta.calendar.dto.CalendarResponseDto;
import com.sparta.calendar.entity.Calendar;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class CalendarController {

    private final JdbcTemplate jdbcTemplate;

    public CalendarController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostMapping("/calendar")
    public CalendarResponseDto createCalendar(@RequestBody CalendarRequestDto requestDto) {
        // RequestDto -> Entity
        Calendar calendar = new Calendar(requestDto);

        // DB 저장
        KeyHolder keyHolder = new GeneratedKeyHolder(); // 기본 키를 반환받기 위한 객체

        String sql = "INSERT INTO calendar (task, username, password, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update( con -> {
                    PreparedStatement preparedStatement = con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);

                    preparedStatement.setString(1, calendar.getTask());
                    preparedStatement.setString(2, calendar.getUsername());
                    preparedStatement.setString(3, calendar.getPassword());
                    preparedStatement.setString(4, String.valueOf(calendar.getCreatedAt()));
                    preparedStatement.setString(5, String.valueOf(calendar.getUpdatedAt()));

                    return preparedStatement;
                },
                keyHolder);

        // DB Insert 후 받아온 기본키 확인
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        calendar.setId(id);

        // Entity -> ResponseDto
        CalendarResponseDto calendarResponseDto = new CalendarResponseDto(calendar);

        return calendarResponseDto;
    }

    @GetMapping("/calendar")
    public List<CalendarResponseDto> getCalendar() {
        // DB 조회
        String sql = "SELECT * FROM calendar";

        return jdbcTemplate.query(sql, new RowMapper<CalendarResponseDto>() {
            @Override
            public CalendarResponseDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                // SQL 의 결과로 받아온 Memo 데이터들을 MemoResponseDto 타입으로 변환해줄 메서드
                Long calendar_id = rs.getLong("calendar_id");
                String task = rs.getString("task");
                String username = rs.getString("username");
                LocalDateTime created_at = LocalDateTime.parse(rs.getString("created_at"));
                LocalDateTime updated_at = LocalDateTime.parse(rs.getString("updated_at"));
                return new CalendarResponseDto(calendar_id, task, username, created_at, updated_at);
            }
        });
    }

    @PutMapping("/calendar/{calendar_id}")
    public Long updateCalendar(@PathVariable Long calendar_id, @RequestBody CalendarRequestDto requestDto, @RequestParam String password) throws Exception {
        // 해당 메모가 DB에 존재하는지 확인
        Calendar calendar = findById(calendar_id);
        if(checkPassword(calendar_id,password)) {
            // memo 내용 수정
            String sql = "UPDATE calendar SET task = ?, username = ?, updated_at = ? WHERE calendar_id = ?";
            jdbcTemplate.update(sql, requestDto.getTask(), requestDto.getUsername(), LocalDateTime.now(), calendar_id);

            return calendar_id;
        } else {
            throw new Exception("선택한 캘린더는 존재하지 않습니다.");
        }
    }

    @DeleteMapping("/calendar/{calendar_id}")
    public Long deleteCalendar(@PathVariable Long calendar_id, @RequestParam String password) throws Exception {
        // 해당 메모가 DB에 존재하는지 확인
        Calendar calendar = findById(calendar_id);
        if(checkPassword(calendar_id,password)) {
            // memo 삭제
            String sql = "DELETE FROM calendar WHERE calendar_id = ?";
            jdbcTemplate.update(sql, calendar_id);

            return calendar_id;
        } else {
            throw new Exception("선택한 캘린더는 존재하지 않습니다.");
        }
    }

    private Calendar findById(Long calendar_id) {
        // DB 조회
        String sql = "SELECT * FROM calendar WHERE calendar_id = ?";

        return jdbcTemplate.query(sql, resultSet -> {
            if(resultSet.next()) {
                Calendar calendar = new Calendar();
                calendar.setTask(resultSet.getString("task"));
                calendar.setUsername(resultSet.getString("username"));
                calendar.setCreatedAt(LocalDateTime.parse(resultSet.getString("created_at")));
                calendar.setUpdatedAt(LocalDateTime.parse(resultSet.getString("updated_at")));
                return calendar;
            } else {
                return null;
            }
        }, calendar_id);
    }
    private boolean checkPassword(Long calendar_id, String password) {
        String sql = "SELECT COUNT(*) FROM calendar WHERE calendar_id = ? AND password = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, calendar_id, password);
        return count != null && count > 0;
    }
}