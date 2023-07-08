package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Objects;

@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;
    private final String usersSql = "select * from users";

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;

    }

    @Override
    public User createUser(User user) {
        final String sql = "insert into users (name, login, birthday, email) values (?, ?, ?, ?)";
        KeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement preparedStatement = connection.prepareStatement(sql,
                    new String[]{"id"});
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getLogin());
            preparedStatement.setObject(3, user.getBirthday());
            preparedStatement.setString(4, user.getEmail());

            return preparedStatement;
        }, generatedKeyHolder);

        int userId = Objects.requireNonNull(generatedKeyHolder.getKey()).intValue();

        user.setId(userId);

        return user;
    }

    @Override
    public User getUserById(Integer userId) {
        try {
            return jdbcTemplate.queryForObject(usersSql.concat(" where id = ?"), new UserMapper(), userId);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Collection<User> getAllUsers() {
        return jdbcTemplate.query(usersSql, new UserMapper());
    }

    @Override
    public User updateUser(User user) {
        final String sql = "update users set name = ?, login = ?, birthday = ?, email = ? where id = ?";

        jdbcTemplate.update(
                sql,
                user.getName(), user.getLogin(), user.getBirthday(), user.getEmail(), user.getId()
        );

        return user;
    }

    @Override
    public Collection<User> getUserFriends(Integer userId) {
        final String sql = "select * from users where id in (select f.friend_id from users u join friendships f " +
                "on u.id = f.user_id where u.id = ?)";

        return jdbcTemplate.query(sql, new UserMapper(), userId);
    }

    @Override
    public Collection<User> getCommonFriends(Integer user1Id, Integer user2Id) {
        final String sql = "select * from users where id in (select friend_id from users u join friendships f on " +
                "u.id = f.user_id where u.id = ?) and id in (select friend_id from users u join friendships f on " +
                "u.id = f.user_id where u.id = ?)";

        return jdbcTemplate.query(sql, new UserMapper(), user1Id, user2Id);
    }
}
