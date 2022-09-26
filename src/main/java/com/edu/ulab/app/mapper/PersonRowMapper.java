package com.edu.ulab.app.mapper;


import com.edu.ulab.app.entity.Person;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonRowMapper implements RowMapper<Person> {

    @Override
    public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
        Person person = new Person();
        person.setId(rs.getLong("ID"));
        person.setFullName(rs.getString("FULL_NAME"));
        person.setTitle(rs.getString("TITLE"));
        person.setAge(rs.getInt("AGE"));
        return person;
    }
}
