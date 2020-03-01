package com.marketing.web.services.user;

import com.marketing.web.enums.RoleType;
import com.marketing.web.models.State;
import com.marketing.web.models.User;

import java.util.Collection;
import java.util.List;

public interface UserService {

    User findByUserName(String userName);

    User findByEmail(String email);

    User findByResetToken(String token);

    boolean checkUserByEmail(String email);

    boolean canRegister(User user);

    List<User> findAll();

    List<User> findAllByRole(RoleType roleType);

    List<User> findAllByRoleAndStatus(RoleType roleType,boolean status);

    List<User> findAllByStatus(boolean status);

    User findById(Long id);

    User findByUUID(String uuid);

    User create(User user, RoleType roleType);

    User update(Long id, User updatedUser);

    void delete(User user);

    User getLoggedInUser();

    User changePassword(User user, String password);

    boolean loginControl(String username, String password);

    User findByActivationToken(String activationToken);

    List<User> findAllByStatesAndRole(List<State> activeStates, RoleType roleType);
}
