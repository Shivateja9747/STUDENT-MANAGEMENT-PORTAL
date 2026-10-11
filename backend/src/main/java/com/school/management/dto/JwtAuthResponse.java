package com.school.management.dto;

import com.school.management.entity.Role;

public class JwtAuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private Long id;
    private String username;
    private String name;
    private Role role;
    private boolean mustChangePassword;

    public JwtAuthResponse() {
    }

    public JwtAuthResponse(String token, String tokenType, Long id, String username, String name, Role role, boolean mustChangePassword) {
        this.token = token;
        this.tokenType = tokenType != null ? tokenType : "Bearer";
        this.id = id;
        this.username = username;
        this.name = name;
        this.role = role;
        this.mustChangePassword = mustChangePassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    public static JwtAuthResponseBuilder builder() {
        return new JwtAuthResponseBuilder();
    }

    public static class JwtAuthResponseBuilder {
        private String token;
        private String tokenType = "Bearer";
        private Long id;
        private String username;
        private String name;
        private Role role;
        private boolean mustChangePassword;

        public JwtAuthResponseBuilder token(String token) {
            this.token = token;
            return this;
        }

        public JwtAuthResponseBuilder tokenType(String tokenType) {
            this.tokenType = tokenType;
            return this;
        }

        public JwtAuthResponseBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public JwtAuthResponseBuilder username(String username) {
            this.username = username;
            return this;
        }

        public JwtAuthResponseBuilder name(String name) {
            this.name = name;
            return this;
        }

        public JwtAuthResponseBuilder role(Role role) {
            this.role = role;
            return this;
        }

        public JwtAuthResponseBuilder mustChangePassword(boolean mustChangePassword) {
            this.mustChangePassword = mustChangePassword;
            return this;
        }

        public JwtAuthResponse build() {
            return new JwtAuthResponse(token, tokenType, id, username, name, role, mustChangePassword);
        }
    }
}
