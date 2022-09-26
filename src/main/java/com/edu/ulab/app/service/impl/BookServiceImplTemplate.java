package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.BookRowMapper;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {

    private static final BookRowMapper rowMapper = new BookRowMapper();
    private final JdbcTemplate jdbcTemplate;
    private final BookMapper bookMapper;

    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate, BookMapper bookMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String INSERT_BOOK_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(INSERT_BOOK_SQL, new String[]{"id"});
                    ps.setString(1, bookDto.getTitle());
                    ps.setString(2, bookDto.getAuthor());
                    ps.setLong(3, bookDto.getPageCount());
                    ps.setLong(4, bookDto.getUserId());
                    return ps;
                },
                keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("createBook from BookServiceImplTemplate successfully: {}", bookDto);
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        BookDto bookById = getBookById(bookDto.getId());
        bookById.setUserId(bookDto.getUserId());
        bookById.setAuthor(bookDto.getAuthor());
        bookById.setTitle(bookDto.getTitle());
        bookById.setPageCount(bookDto.getPageCount());

        String UPDATE_BOOK_SQL =
                "UPDATE BOOK SET ID = ?, TITLE = ?, AUTHOR = ?, PAGE_COUNT = ?, USER_ID = ? WHERE ID = ?";

        jdbcTemplate.update(UPDATE_BOOK_SQL,
                bookDto.getId(),
                bookById.getTitle(),
                bookById.getAuthor(),
                bookById.getPageCount(),
                bookById.getUserId(), bookDto.getId());

        log.info("updateBook from BookServiceImplTemplate successfully: {}", bookById);
        return bookById;
    }

    @Override
    public BookDto getBookById(Long id) {
        String GET_BOOK_SQL = "SELECT * FROM BOOK WHERE ID = ?";
        List<Book> query = jdbcTemplate.query(GET_BOOK_SQL, rowMapper, id);
        Book book = query.stream().findAny().orElse(null);

        BookDto bookDto = bookMapper.bookToBookDto(book);

        log.info("getBookById from BookServiceImplTemplate successfully: {}", bookDto);
        return bookDto;
    }

    @Override
    public void deleteBookById(Long id) {
        String DELETE_BOOK_SQL = "DELETE FROM BOOK WHERE ID = ?";
        int deleteCount = jdbcTemplate.update(DELETE_BOOK_SQL, id);

        log.info("deleteBookById from BookServiceImplTemplate successfully: {}", deleteCount);
    }

    @Override
    public List<Long> getBookByUserId(Long id) {
        String GET_BOOK_BY_USER_ID = "SELECT * FROM BOOK WHERE USER_ID = ?";
        List<Book> query = jdbcTemplate.query(GET_BOOK_BY_USER_ID, rowMapper, id);
        List<Long> listBookByUserId = query.stream().map(Book::getId).toList();
        log.info("getBookByUserId from BookServiceImplTemplate successfully: {}", listBookByUserId);

        return listBookByUserId;
    }

    @Override
    public void deleteBookByUserId(Long userId) {
        String DELETE_BOOK_BY_USER_ID = "DELETE FROM BOOK WHERE USER_ID = ?";

        int deleteCount = jdbcTemplate.update(DELETE_BOOK_BY_USER_ID, userId);
        log.info("deleteBookByUserId from BookServiceImplTemplate successfully: {}", deleteCount);
    }
}
