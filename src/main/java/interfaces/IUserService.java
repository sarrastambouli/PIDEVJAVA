package interfaces;

import entities.User;
import java.sql.SQLException;
import java.util.List;

public interface IUserService {
    void addUser(User u) throws SQLException;
    void updateUser(User u, int id) throws SQLException;
    void deleteUser(int id) throws SQLException;
    List<User> getAllUsers() throws SQLException;
    User getUserById(int id) throws SQLException;
    User getUserByEmail(String email) throws SQLException;
}
