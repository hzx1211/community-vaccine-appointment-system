package com.vaccine.interceptor;

import com.vaccine.annotation.RequireRole;
import com.vaccine.common.UserContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import static org.assertj.core.api.Assertions.assertThat;

class RoleInterceptorTest {

    private final RoleInterceptor interceptor = new RoleInterceptor();

    @AfterEach
    void clearUserContext() {
        UserContext.remove();
    }

    @Test
    void rejectsAUserWithoutTheRequiredRole() throws Exception {
        UserContext.set(1L, "normal-user", "user");
        HandlerMethod handler = new HandlerMethod(new AdminOnlyEndpoint(),
                AdminOnlyEndpoint.class.getDeclaredMethod("save"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean allowed = interceptor.preHandle(new MockHttpServletRequest(), response, handler);

        assertThat(allowed).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).contains("\"code\":403");
    }

    @Test
    void allowsTheRequiredRole() throws Exception {
        UserContext.set(2L, "system-admin", "admin");
        HandlerMethod handler = new HandlerMethod(new AdminOnlyEndpoint(),
                AdminOnlyEndpoint.class.getDeclaredMethod("save"));

        boolean allowed = interceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), handler);

        assertThat(allowed).isTrue();
    }

    private static class AdminOnlyEndpoint {
        @RequireRole("admin")
        public void save() {
        }
    }
}
