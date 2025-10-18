package com.laborplanner.backend.service.interfaces;

import com.laborplanner.backend.model.User;

public interface IUserTokenService {
  User getUserByUuid(String userUuid);
}
