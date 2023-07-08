package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class UserMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User()
                .toBuilder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .login(rs.getString("login"))
                .birthday(Objects.requireNonNull(rs.getDate("birthday")).toLocalDate())
                .email(rs.getString("email"))
                .build();
    }
}