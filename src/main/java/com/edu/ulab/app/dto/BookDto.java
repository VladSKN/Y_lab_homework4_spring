package com.edu.ulab.app.dto;

import com.edu.ulab.app.entity.Person;
import lombok.Data;

@Data
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private long pageCount;
    private Long userId;
}
