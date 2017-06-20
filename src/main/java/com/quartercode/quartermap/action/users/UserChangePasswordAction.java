/*
 * This file is part of QuarterMAP.
 * Copyright (c) 2014 QuarterCode <http://quartercode.com/>
 *
 * QuarterMAP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QuarterMAP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with QuarterMAP. If not, see <http://www.gnu.org/licenses/>.
 */

package com.quartercode.quartermap.action.users;

import org.apache.commons.codec.digest.DigestUtils;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.Preparable;
import com.quartercode.quartermap.dto.user.Role;
import com.quartercode.quartermap.dto.user.User;
import com.quartercode.quartermap.exception.CancelWithErrorException;
import com.quartercode.quartermap.exception.CancelWithLoginException;
import com.quartercode.quartermap.service.UserService;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings ("serial")
@Getter
@Setter
public class UserChangePasswordAction extends ActionSupport implements Preparable {

    private final UserService userService = new UserService();

    private String            userId;
    private User              user;

    private String            newPassword;
    private String            newPasswordRepetition;

    @Override
    public void prepare() throws Exception {

        user = userService.getUserByName(userId);

        if (user == null) {
            // Invalid userId
            throw new CancelWithErrorException();
        }

        User authenticated = (User) ActionContext.getContext().getSession().get("authenticated");
        if (!authenticated.getRole().getAuthorizations().get(Role.ADMIN) && user.getId() != authenticated.getId()) {
            // Unauthorized access
            throw new CancelWithLoginException();
        }
    }

    @Override
    public String execute() throws Exception {

        if (!newPassword.equals(newPasswordRepetition)) {
            addFieldError("newPasswordRepetition", "Not the same password");
            return INPUT;
        }

        user.setPassword(DigestUtils.sha256Hex(newPassword + user.getSalt()));
        userService.updateUser(user);

        return SUCCESS;
    }

}
