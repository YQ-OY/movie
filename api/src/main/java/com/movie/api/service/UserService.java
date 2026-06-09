package com.movie.api.service;

import com.movie.api.model.dto.LoginDto;
import com.movie.api.model.entity.User;
import com.movie.api.model.vo.PageResult;

import java.util.List;

public interface UserService {

    User login(LoginDto dto) throws Exception;

    List<User> findAll();

    User findById(String id);

    User update(User user) throws Exception;

    User save(User user) throws Exception;

    User findByUsername(String username);

    void deleteById(String id);

    PageResult<User> findByPage(Integer page, Integer size, String keyword);
}
