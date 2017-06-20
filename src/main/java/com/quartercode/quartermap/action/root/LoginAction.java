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

package com.quartercode.quartermap.action.root;

import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;
import com.opensymphony.xwork2.ActionContext;
import com.quartercode.quartermap.action.DynamicRedirectAction;
import com.quartercode.quartermap.dto.user.User;
import com.quartercode.quartermap.service.UserService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings ("serial")
@Getter
@Setter
public class LoginAction extends DynamicRedirectAction {

    @Getter (AccessLevel.NONE)
    private final UserService userService = new UserService();

    private String            username;
    private String            password;

    @Override
    public String execute() throws Exception {

        User user = userService.getUserByName(username);

        if (user == null) {
            addFieldError("username", "User doesn't exist");
            return INPUT;
        } else if (!DigestUtils.sha256Hex(password + user.getSalt()).equals(user.getPassword())) {
            addFieldError("password", "Wrong password");
            return INPUT;
        } else {
            // Valid credentials
            Map<String, Object> session = ActionContext.getContext().getSession();
            session.put("authenticated", user);
            return SUCCESS;
        }
    }

}
