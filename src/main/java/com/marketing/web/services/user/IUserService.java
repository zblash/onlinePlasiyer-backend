package com.marketing.web.services.user;

import com.marketing.web.enums.RoleType;
import com.marketing.web.models.User;

import java.util.List;

public interface IUserService {

    User findByUserName(String userName);

    List<User> findAll();

    List<User> findAllByRole(RoleType roleType);

    List<User> findAllByRoleAndStatus(RoleType roleType,boolean status);

    List<User> findAllByStatus(boolean status);

    User findById(Long id);

    User findByUUID(String uuid);

    User create(User user, RoleType roleType);

    User update(Long id, User updatedUser);

    void delete(User user);
}
