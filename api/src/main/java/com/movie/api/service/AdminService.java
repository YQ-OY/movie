package com.movie.api.service;

import com.movie.api.model.dto.LoginDto;
import com.movie.api.model.entity.Admin;

import java.util.Map;

public interface AdminService {

    String login(LoginDto loginDto) throws Exception;

    Admin update(Admin admin);

    Admin getCurrent(String token);
}
