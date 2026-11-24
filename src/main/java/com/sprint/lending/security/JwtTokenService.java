package com.sprint.lending.security;

import com.sprint.lending.entity.User;

public interface JwtTokenService {

    String issueToken(User user);
}
