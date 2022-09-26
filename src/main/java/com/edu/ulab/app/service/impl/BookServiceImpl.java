package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);
        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Optional<Book> byIdForUpdate = bookRepository.findByIdForUpdate(bookDto.getId());
        if (byIdForUpdate.isEmpty()) {          // альтернатива нужна
            log.error("updateBook from BookServiceImpl an error has occurred");
            return bookDto;
        }
        Book book = byIdForUpdate.get();
        book.setTitle(bookDto.getTitle());
        book.setId(bookDto.getId());                //нужно ли сетить id
        book.setAuthor(bookDto.getAuthor());
        book.setPageCount(bookDto.getPageCount());
        book.setUserId(bookDto.getUserId());

        BookDto bookToBookDto = bookMapper.bookToBookDto(book);
        log.info("Mapped book from BookServiceImpl successfully: {}", book);

        Book savedBook = bookRepository.save(book);
        log.info("updateBook from BookServiceImpl successfully: {}", savedBook);

        return bookToBookDto;
    }

    @Override
    public BookDto getBookById(Long id) {
        Optional<Book> bookById = bookRepository.findById(id);
        BookDto bookDto = bookMapper.bookToBookDto(bookById.orElse(null)); // норм ли так делать?

        log.info("getBookById from BookServiceImpl successfully: {}", id);
        return bookDto;
    }

    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
        log.info("deleteBookById from BookServiceImpl successfully: {}", id);
    }

    @Override
    public List<Long> getBookByUserId(Long id) {
        Optional<Book> byId = bookRepository.findById(id);

        List<Long> longList = byId.stream()
                .map(Book::getId)
                .toList();
        log.info("getBookByUserId from BookServiceImpl successfully: {}", longList);
        return longList;
    }

    @Override
    public void deleteBookByUserId(Long userId) {
        bookRepository.deleteByUserId(userId);
    }
}
