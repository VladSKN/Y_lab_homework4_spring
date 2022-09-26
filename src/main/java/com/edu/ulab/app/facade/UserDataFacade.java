package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.impl.BookServiceImplTemplate;
import com.edu.ulab.app.service.impl.UserServiceImplTemplate;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserDataFacade {
    private final UserServiceImplTemplate userService;
    private final BookServiceImplTemplate bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserServiceImplTemplate userService,
                          BookServiceImplTemplate bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

        List<Long> bookIdList = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(BookDto::getId)
                .toList();
        log.info("Collected book ids: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(createdUser.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public UserBookResponse updateUserWithBooks(UserBookRequest userBookRequest) {
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        userService.updateUser(userDto);
        log.info("updateUser from UserDataFacade successfully: {}", userDto);

        List<Long> bookIdList = userBookRequest.getBookRequests().stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .map(bookService::updateBook)
                .map(BookDto::getId)
                .toList();

        log.info("updateBook from UserDataFacade successfully: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(userDto.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public UserBookResponse getUserWithBooks(Long userId) {
        UserDto userById = userService.getUserById(userId);
        userService.updateUser(userById);

        List<Long> userBooks = bookService.getBookByUserId(userId);

        log.info("getUserWithBooks from UserDataFacade successfully: {}, {}", userById, userBooks);

        return UserBookResponse.builder()
                .userId(userById.getId())
                .booksIdList(userBooks)
                .build();
    }

    public void deleteUserWithBooks(Long userId) {
        userService.deleteUserById(userId);
        bookService.deleteBookByUserId(userId);
        log.info("deleteUserWithBooks from UserDataFacade successfully: {}", userId);
    }
}
