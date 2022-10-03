package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("Создание пользователя. Должно пройти успешно.")
    void savePerson_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person person = new Person();
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");

        Person savedPerson = new Person();
        savedPerson.setId(1L);
        savedPerson.setFullName("test name");
        savedPerson.setAge(11);
        savedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");

        //when

        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.save(person)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);

        //then

        UserDto userDtoResult = userService.createUser(userDto);
        assertEquals(1L, userDtoResult.getId());
    }

    @Test
    @DisplayName("Обновление пользователя. Должно пройти успешно.")
    void updateUser_Test() {
        //Given
        Person person = new Person();

        person.setAge(30);
        person.setTitle("reader2");
        person.setFullName("new User");
        person.setId(1002L);

        Person newPerson = new Person();

        newPerson.setAge(55);
        newPerson.setTitle("reader3");
        newPerson.setFullName("User");
        newPerson.setId(1002L);

        UserDto newUserDto = new UserDto();

        newUserDto.setAge(55);
        newUserDto.setTitle("reader3");
        newUserDto.setFullName("User");
        newUserDto.setId(1002L);

        //When

        when(userRepository.findByIdForUpdate(person.getId())).thenReturn(Optional.of(person));
        when(userMapper.personToUserDto(person)).thenReturn(newUserDto);
        when(userRepository.save(person)).thenReturn(newPerson);
        UserDto result = userService.updateUser(newUserDto);

        //Then
        assertEquals(55, result.getAge());
        assertEquals("reader3", result.getTitle());
        assertEquals("User", result.getFullName());
        assertEquals(1002L, result.getId());
        verify(userRepository).save(person);
    }

    @Test
    @DisplayName("Обновление пользователя, если пользователь null. Должно пройти успешно.")
    void updateUser_if_user_isNull_Test() {
        //Given
        UserDto person = null;
        //When

        //Then
        Assertions.assertThrows(NullPointerException.class, () -> {
            userService.updateUser(person);
        });
    }

    @Test
    @DisplayName("Обновление пользователя, если такого пользователя нет, Должно пройти успешно.")
    void updateUser_if_user_notExist_Test() {
        //Given
        Person person = new Person();

        person.setAge(30);
        person.setTitle("reader2");
        person.setFullName("new User");
        person.setId(1002L);

        UserDto userDto = new UserDto();

        userDto.setAge(30);
        userDto.setTitle("reader2");
        userDto.setFullName("new User");
        userDto.setId(1002L);
        //When
        UserDto result = userService.updateUser(userDto);
        //Then
        assertEquals(30, result.getAge());
        assertEquals("reader2", result.getTitle());
        assertEquals("new User", result.getFullName());
        assertEquals(1002L, result.getId());
        verify(userMapper, never()).personToUserDto(person);
        verify(userRepository, never()).save(person);

    }

    @Test
    @DisplayName("Получение пользователя по id. Должно пройти успешно.")
    void getUserById_Test() {
        //Given
        UserDto userDto = new UserDto();
        userDto.setAge(55);
        userDto.setTitle("reader3");
        userDto.setFullName("User");
        userDto.setId(1L);

        Person person = new Person();
        person.setAge(55);
        person.setTitle("reader3");
        person.setFullName("User");
        person.setId(1L);

        //When
        when(userRepository.findById(1L)).thenReturn(Optional.of(person));
        when(userMapper.personToUserDto(person)).thenReturn(userDto);

        //Then

        UserDto resultUser = userService.getUserById(1L);
        assertEquals(55, resultUser.getAge());
        assertEquals("reader3", resultUser.getTitle());
        assertEquals("User", resultUser.getFullName());
        assertEquals(1L, resultUser.getId());
        verify(userRepository).findById(1L);
        verify(userMapper).personToUserDto(person);
    }

    @Test
    @DisplayName("Получение пользователя по id, если такого пользователя нет. Должно пройти успешно.")
    void getUserById_if_user_isEmpty_Test() {
        //Given
        Person person = new Person();
        person.setAge(55);
        person.setTitle("reader3");
        person.setFullName("User");
        person.setId(1L);

        //When

        //Then
        UserDto userById = userService.getUserById(person.getId());
        assertNull(userById);
    }

    @Test
    @DisplayName("Удаление пользователя по id. Должно пройти успешно.")
    void deleteUserById_Test() {
        //Given
        Person person = new Person();
        person.setAge(55);
        person.setTitle("reader3");
        person.setFullName("User");
        person.setId(1L);
        //When

        //Then
        userService.deleteUserById(person.getId());
        verify(userRepository).deleteById(person.getId());
    }
}
