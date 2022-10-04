package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты репозитория {@link UserRepository}.
 */

@SystemJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить юзера. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void insertPerson_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");

        //When
        Person result = userRepository.save(person);

        //Then
        assertThat(result.getAge()).isEqualTo(111);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Обновить юзера. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updatePerson_thenAssertDmlCount() {
        //Given
        Optional<Person> byId = userRepository.findById(1001L);
        byId.get().setAge(36);
        byId.get().setTitle("reader2");
        byId.get().setFullName("default user2");
        byId.get().setCount(2);

        //When
        Person result = userRepository.save(byId.get());

        //Then
        assertThat(result.getId()).isEqualTo(byId.get().getId());
        assertThat(result.getCount()).isEqualTo(byId.get().getCount());
        assertThat(result.getTitle()).isEqualTo(byId.get().getTitle());
        assertThat(result.getAge()).isEqualTo(byId.get().getAge());
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить юзера. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void get_person_byId_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setCount(1);
        person.setId(1001L);
        person.setAge(55);
        person.setTitle("reader");
        person.setFullName("default user");

        //When
        Optional<Person> byId = userRepository.findById(person.getId());

        //Then
        assertThat(byId.get().getId()).isEqualTo(person.getId());
        assertThat(byId.get().getCount()).isEqualTo(person.getCount());
        assertThat(byId.get().getTitle()).isEqualTo(person.getTitle());
        assertThat(byId.get().getAge()).isEqualTo(person.getAge());
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить Всех юзеров по id. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void get_all_Person_byId_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setCount(1);
        person.setId(1001L);
        person.setAge(55);
        person.setTitle("reader");
        person.setFullName("default user");

        Book book = new Book();
        book.setPerson(person);
        book.setAuthor("author");
        book.setPageCount(5500);
        book.setId(2002L);
        book.setTitle("default book");

        Book book2 = new Book();
        book2.setPerson(person);
        book2.setAuthor("on more author");
        book2.setPageCount(6655);
        book2.setId(3003L);
        book2.setTitle("more default book");

        Set<Book> bookSet = new HashSet<>();
        bookSet.add(book);
        bookSet.add(book2);
        person.setBookSet(bookSet);

        //When
        Iterable<Person> allPerson = userRepository.findAllById(Collections.singleton(person.getId()));

        //Then
        Person actualPerson = allPerson.iterator().next();
        assertEquals(1, actualPerson.getCount());
        assertEquals(1001L, actualPerson.getId());
        assertEquals(55, actualPerson.getAge());
        assertEquals("reader", actualPerson.getTitle());
        assertEquals("default user", actualPerson.getFullName());
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Удалить юзера. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deletePerson_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setId(1001L);
        person.setAge(55);
        person.setTitle("reader");
        person.setFullName("default user");
        person.setCount(1);

        //When
        userRepository.delete(person);

        //Then
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }
}
