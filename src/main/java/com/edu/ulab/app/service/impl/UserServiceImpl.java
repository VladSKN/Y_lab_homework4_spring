package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);
        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);
        return userMapper.personToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        Optional<Person> byIdForUpdate = userRepository.findByIdForUpdate(userDto.getId());
        if (byIdForUpdate.isEmpty()) {               // альтернатива нужна
            log.error("updateUser from UserServiceImpl an error has occurred");
            return userDto;
        }

        Person person = byIdForUpdate.get();
        person.setId(userDto.getId());          // нужно ли сетить id
        person.setTitle(userDto.getTitle());
        person.setAge(userDto.getAge());
        person.setFullName(userDto.getFullName());

        userRepository.save(person);

        UserDto userDtoMapped = userMapper.personToUserDto(person);
        log.info("Mapped personToPersonDto from UserServiceImpl successfully: {}", person);

        Person savedPerson = userRepository.save(person);
        log.info("updateUser from UserServiceImpl successfully: {}", savedPerson);

        return userDtoMapped;
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<Person> userById = userRepository.findById(id);
        UserDto userDto = userMapper.personToUserDto(userById.orElse(null)); // норм ли так делать?
        log.info("getUserById from UserServiceImpl successfully: {}", id);
        return userDto;
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
        log.info("deleteUserById from UserServiceImpl successfully: {}", id);
    }
}
