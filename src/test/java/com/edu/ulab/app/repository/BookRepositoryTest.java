package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.Optional;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты репозитория {@link BookRepository}.
 */
@SystemJpaTest
public class BookRepositoryTest {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить книгу и автора. Число select должно равняться 2")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void findAllBadges_thenAssertDmlCount() {
        //Given

        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setAuthor("Test Author");
        book.setTitle("test");
        book.setPageCount(1000);
        book.setPerson(savedPerson);

        //When
        Book result = bookRepository.save(book);

        //Then
        assertThat(result.getPageCount()).isEqualTo(1000);
        assertThat(result.getTitle()).isEqualTo("test");
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Обновить книгу. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void update_book_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");
        person.setId(1001L);

        Book book = new Book();
        book.setId(2002L);
        book.setAuthor("Test Author");
        book.setTitle("test");
        book.setPageCount(1000);
        book.setPerson(person);

        //When
        Book saveBook = bookRepository.save(book);

        //Then
        assertThat(saveBook.getPageCount()).isEqualTo(1000);
        assertThat(saveBook.getTitle()).isEqualTo("test");
        assertThat(saveBook.getId()).isEqualTo(2002L);
        assertThat(saveBook.getAuthor()).isEqualTo("Test Author");
        assertThat(saveBook.getPerson().getId()).isEqualTo(person.getId());
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить книгу по id книгу. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void get_book_byId_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");
        person.setId(1001L);

        Book book = new Book();
        book.setId(2002L);
        book.setAuthor("author");
        book.setTitle("default book");
        book.setPageCount(5500);
        book.setPerson(person);
        //When
        Optional<Book> byId = bookRepository.findById(book.getId());

        //Then
        assertThat(byId.get().getPageCount()).isEqualTo(book.getPageCount());
        assertThat(byId.get().getTitle()).isEqualTo(book.getTitle());
        assertThat(byId.get().getId()).isEqualTo(book.getId());
        assertThat(byId.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(byId.get().getPerson().getId()).isEqualTo(person.getId());
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить все книги по id. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void get_all_book_byId_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");
        person.setId(1001L);

        Book book = new Book();
        book.setId(2002L);
        book.setAuthor("author");
        book.setTitle("default book");
        book.setPageCount(5500);
        book.setPerson(person);

        //When
        Iterable<Book> allById = bookRepository.findAllById(Collections.singleton(book.getId()));

        //Then
        Book actualBook = allById.iterator().next();
        assertThat(actualBook.getPageCount()).isEqualTo(book.getPageCount());
        assertThat(actualBook.getTitle()).isEqualTo(book.getTitle());
        assertThat(actualBook.getId()).isEqualTo(book.getId());
        assertThat(actualBook.getAuthor()).isEqualTo(book.getAuthor());
        assertThat(actualBook.getPerson().getId()).isEqualTo(person.getId());
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Удалить книгу по id. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void delete_book_byId_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");
        person.setId(1001L);

        Book book = new Book();
        book.setId(2002L);
        book.setAuthor("author");
        book.setTitle("default book");
        book.setPageCount(5500);
        book.setPerson(person);

        //When
        bookRepository.deleteById(book.getId());

        //Then
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Сохранение книги, если нет person. Должно пройти успешно.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void save_book_if_not_person_Test() {
        //Given
        Book book = new Book();
        book.setId(2003L);
        book.setAuthor("author");
        book.setTitle("default book");
        book.setPageCount(5500);

        //When

        //Then
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            bookRepository.save(book);
        });
    }
}
