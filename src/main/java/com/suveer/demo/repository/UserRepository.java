package com.suveer.demo.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createUser(String username, String password, String email) {
        String sql = "INSERT INTO users(username, password, email) VALUES(?, ?, ?)";
        jdbcTemplate.update(sql, username, password, email);
    }

    public String getUserEmail(String username) {
        String sql = "SELECT email FROM users WHERE username = ?";
        return jdbcTemplate.queryForObject(sql, String.class, username);
    }

    public boolean validateUser(String username, String password) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
        Integer count = jdbcTemplate.queryForObject(sql, new Object[]{username, password}, Integer.class);
        return count != null && count > 0;
    }
}