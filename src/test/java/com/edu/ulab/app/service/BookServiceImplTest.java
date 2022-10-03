package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
public class BookServiceImplTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookRepository bookRepository;

    @Mock
    BookMapper bookMapper;

    @Test
    @DisplayName("Создание книги. Должно пройти успешно.")
    void saveBook_Test() {
        //given
        Person person = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(person.getId());
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(person.getId());
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setPerson(person);

        //when

        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);


        //then
        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
    }

    @Test
    @DisplayName("Обновление книги. Должно пройти успешно.")
    void updateBook_Test() {
        //Given
        Person person = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setUserId(person.getId());
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        Book book = new Book();
        book.setId(1L);
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book newBook = new Book();
        newBook.setId(1L);
        newBook.setPageCount(2000);
        newBook.setTitle("test new title");
        newBook.setAuthor("test new author");
        newBook.setPerson(person);

        BookDto newBookDto = new BookDto();
        newBookDto.setId(1L);
        newBookDto.setPageCount(2000);
        newBookDto.setTitle("test new title");
        newBookDto.setAuthor("test new author");
        newBookDto.setUserId(person.getId());

        //When

        when(bookRepository.findByIdForUpdate(bookDto.getId())).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookDto(book)).thenReturn(newBookDto);
        when(bookRepository.save(book)).thenReturn(newBook);
        BookDto result = bookService.updateBook(newBookDto);

        //Then
        assertEquals(2000, result.getPageCount());
        assertEquals("test new title", result.getTitle());
        assertEquals("test new author", result.getAuthor());
        assertEquals(person.getId(), result.getUserId());
        verify(bookRepository).save(book);
    }

    @Test
    @DisplayName("Обновление книги, если книга null. Должно пройти успешно.")
    void updateBook_if_book_isNull_Test() {
        //Given
        BookDto bookDto = null;
        //When

        //Then
        Assertions.assertThrows(NullPointerException.class, () -> {
            bookService.updateBook(bookDto);
        });
    }

    @Test
    @DisplayName("Обновление книги, если такой книги нет, Должно пройти успешно.")
    void updateBook_if_book_notExist_Test() {
        //Given
        Person person = new Person();
        person.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setUserId(person.getId());
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);
        //When
        BookDto result = bookService.updateBook(bookDto);
        //Then
        assertEquals(1000, result.getPageCount());
        assertEquals("test title", result.getTitle());
        assertEquals("test author", result.getAuthor());
        assertEquals(person.getId(), result.getUserId());
        verify(bookMapper, never()).bookToBookDto(book);
        verify(bookRepository, never()).save(book);
    }

    @Test
    @DisplayName("Получение книги по id. Должно пройти успешно.")
    void getBookById_Test() {
        //Given
        Person person = new Person();
        person.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setUserId(person.getId());
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        //When
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookDto(book)).thenReturn(bookDto);

        //Then

        BookDto result = bookService.getBookById(1L);
        assertEquals(1000, result.getPageCount());
        assertEquals("test title", result.getTitle());
        assertEquals("test author", result.getAuthor());
        assertEquals(person.getId(), result.getUserId());
        verify(bookRepository).findById(1L);
        verify(bookMapper).bookToBookDto(book);
    }

    @Test
    @DisplayName("Получение книги по id, если такой книги нет. Должно пройти успешно.")
    void getBookById_if_book_isEmpty_Test() {
        //Given
        Person person = new Person();
        person.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        //When

        //Then
        BookDto bookById = bookService.getBookById(person.getId());
        assertNull(bookById);
    }

    @Test
    @DisplayName("Удаление книги по id. Должно пройти успешно.")
    void deleteBookById_Test() {
        //Given
        Person person = new Person();
        person.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);
        //When

        //Then
        bookService.deleteBookById(book.getId());
        verify(bookRepository).deleteById(book.getId());
    }

    @Test
    @DisplayName("Получение коллекции книг по userId. Должно пройти успешно.")
    void getBookByUserId() {
        //Given
        Person person = new Person();
        person.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setPageCount(800);
        book2.setTitle("test new title");
        book2.setAuthor("test new author");
        book2.setPerson(person);

        List<Long> expected = List.of(1L, 2L);

        Iterable<Book> iterableBook = List.of(book, book2);

        //When
        when(bookRepository.findAllById(Collections.singleton(1L))).thenReturn(iterableBook);

        //Then
        List<Long> actual = bookService.getBookByUserId(person.getId());
        assertIterableEquals(expected, actual);
        verify(bookRepository).findAllById(Collections.singleton(1L));
    }

    @Test
    @DisplayName("Удаление книги по userId. Должно пройти успешно.")
    void deleteBookByPerson_id() {
        //Given
        Person person = new Person();
        person.setId(1L);

        Book book = new Book();
        book.setId(1L);
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);
        //When

        //Then
        bookService.deleteBookByPerson_id(person.getId());
        verify(bookRepository).deleteByPerson_Id(person.getId());
    }
}
