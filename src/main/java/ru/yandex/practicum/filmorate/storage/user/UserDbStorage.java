package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public User addUser(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users").usingGeneratedKeyColumns("id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(toMap(user)).intValue());
        log.info("В базу добавлен новый пользователь. id - {}", user.getId());
        return getUserById(user.getId());
    }

    @Override
    public User updateUser(User user) {
        String sqlQuery = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE id = ?;";
        jdbcTemplate.update(sqlQuery,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());
        log.info("Пользователь обновлен. id - {}", user.getId());
        return user;
    }

    @Override
    public List<User> getUsers() {
        String sqlQuery = "SELECT * FROM users;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUsers);
    }

    @Override
    public void deleteUser(Integer id) {
        jdbcTemplate.update("DELETE FROM users WHERE id=?", id);
        log.info("Пользователь с id {} удален", id);
    }

    @Override
    public boolean checkUserExistInBd(int id) {
        String sqlQuery = "select id, email, login, name, birthday " +
                "FROM users WHERE id = ?;";
        return !jdbcTemplate.query(sqlQuery, this::mapRowToUsers, id).isEmpty();
    }

    @Override
    public User getUserById(int id) {
        String sqlQuery = "select id, email, login, name, birthday " +
                "FROM users WHERE id = ?;";
        return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToUsers, id);
    }

    @Override
    public void addFriend(int userId, int friendId) {
        String sqlQuery = "INSERT into friends (user_Id, friend_Id) values(?, ?);";
        addToFeedAddFriend(userId, friendId);
        jdbcTemplate.update(sqlQuery, userId, friendId);

    }

    @Override
    public List<User> getFriendsList(Integer id) {
        String sqlQuery =
                "SELECT \n" +
                        "u.id , \n" +
                        "u.email,\n" +
                        "u.login,\n" +
                        "u.name, \n" +
                        "u.birthday,\n" +
                        "FROM users u \n" +
                        "JOIN friends f ON f.user_id  = ?\n" +
                        "WHERE u.id  = f.FRIEND_ID ;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToUsers, id);
    }

    @Override
    public List<Integer> getFriendsId(User user) {
        String sqlQuery = "SELECT friend_id FROM friends WHERE user_id = ?;";
        return jdbcTemplate.query(sqlQuery, this::mapRowToFriedIdFromFriends, user.getId());
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?;";
        addToFeedDeleteFriend(userId, friendId);
        jdbcTemplate.update(sql, userId, friendId);

    }

    private Integer mapRowToFriedIdFromFriends(ResultSet resultSet, int rowNum) throws SQLException {
        return resultSet.getInt("friend_id");
    }

    private User mapRowToUsers(ResultSet resultSet, int rowNum) throws SQLException {
        User user = User.builder()
                .id(resultSet.getInt("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
        user.setFriends(new HashSet<>(getFriendsId(user)));
        return user;
    }

    public Map<String, Object> toMap(User user) {
        Map<String, Object> values = new HashMap<>();
        values.put("email", user.getEmail());
        values.put("login", user.getLogin());
        values.put("name", user.getName());
        values.put("birthday", user.getBirthday());
        return values;
    }

    @Override
    public List<Feed> getUserFeed(Integer userId) {
        String sqlQuery = "SELECT * FROM feed WHERE user_id = ?";
        return jdbcTemplate.query(sqlQuery, this::makeFeed, userId);
    }

    private Feed makeFeed(ResultSet rs, int rowNum) throws SQLException {
        long timestampInMillis = rs.getTimestamp("time_stamp").getTime(); // Преобразование в миллисекунды
        return Feed.builder()
                .userId(rs.getInt("user_id"))
                .eventType(rs.getString("event_type"))
                .operation(rs.getString("operation"))
                .eventId(rs.getInt("event_id"))
                .entityId(rs.getInt("entity_id"))
                .timestamp(timestampInMillis) // Используем миллисекунды
                .build();
    }

    private void addToFeedDeleteFriend(Integer userId, Integer friendId) {
        String sql = "INSERT INTO feed (user_id, event_type, operation, entity_id, time_stamp) " +
                "VALUES (?, 'FRIEND', 'REMOVE', ?, ?)";
        long currentTimeMillis = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currentTimeMillis);
        jdbcTemplate.update(sql, userId, friendId, timestamp);
    }

    private void addToFeedAddFriend(Integer userId, Integer friendId) {
        String sql = "INSERT INTO feed (user_id, event_type, operation, entity_id, time_stamp)" +
                " VALUES (?, 'FRIEND', 'ADD', ?, ?)";
        long currentTimeMillis = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(currentTimeMillis);
        jdbcTemplate.update(sql, userId, friendId, timestamp);
    }
}
