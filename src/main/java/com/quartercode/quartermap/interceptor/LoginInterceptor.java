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

package com.quartercode.quartermap.interceptor;

import java.util.Map;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.quartercode.quartermap.dto.user.Role;
import com.quartercode.quartermap.dto.user.User;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings ("serial")
@Getter
@Setter
public class LoginInterceptor extends AbstractInterceptor {

    private Role role;

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {

        Map<String, Object> session = invocation.getInvocationContext().getSession();
        if (session == null || session.get("authenticated") == null) {
            // Display login page without executing the action
            return Action.LOGIN;
        }

        User user = (User) session.get("authenticated");
        if (role != null && !user.getRole().getAuthorizations().get(role)) {
            // Display login page without executing the action
            return Action.LOGIN;
        }

        // Every test succeeds
        invocation.invoke();
        return invocation.getResultCode();
    }

}
