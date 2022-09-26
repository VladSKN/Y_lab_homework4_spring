package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.PersonRowMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {

    private final JdbcTemplate jdbcTemplate;
    private static final PersonRowMapper personRowMapper = new PersonRowMapper();
    private final UserMapper userMapper;

    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        final String INSERT_USER_SQL = "INSERT INTO PERSON(FULL_NAME, TITLE, AGE) VALUES (?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_USER_SQL, new String[]{"id"});
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setLong(3, userDto.getAge());
                    return ps;
                }, keyHolder);

        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("createUser from UserServiceImplTemplate successfully: {}", userDto);
        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        UserDto userById = getUserById(userDto.getId());
        userById.setAge(userDto.getAge());
        userById.setTitle(userDto.getTitle());
        userById.setFullName(userDto.getFullName());

        final String UPDATE_USER_SQL = "UPDATE PERSON SET ID = ?, FULL_NAME = ?, TITLE = ?, AGE = ? WHERE ID = ?";
        jdbcTemplate.update(UPDATE_USER_SQL,
                userDto.getId(),
                userById.getFullName(),
                userById.getTitle(),
                userById.getAge(), userDto.getId());

        log.info("updateUser from UserServiceImplTemplate successfully: {}", userById);
        return userById;
    }

    @Override
    public UserDto getUserById(Long id) {
        final String GET_USER_SQL = "SELECT * FROM PERSON WHERE id = ?";
        List<Person> query = jdbcTemplate.query(GET_USER_SQL, personRowMapper, id);

        Optional<Person> any = query.stream().findAny();
        if (any.isEmpty()) {
            log.error("getUserById from UserServiceImplTemplate user not found");
            return null;
        }
        Person person = any.get();

        UserDto userDto = userMapper.personToUserDto(person);

        log.info("getUserById from UserServiceImplTemplate successfully: {}", userDto);
        return userDto;
    }

    @Override
    public void deleteUserById(Long id) {
        final String DELETE_USER_SQL = "DELETE FROM PERSON WHERE id = ?";
        int deletedCount = jdbcTemplate.update(DELETE_USER_SQL, id);

        log.info("deleteUserById from UserServiceImplTemplate successfully: {}", deletedCount);
    }
}