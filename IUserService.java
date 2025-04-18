package GesUsers.interfaces;

import GesUsers.entities.User;
import java.util.List;

public interface IUserService {
    void addUser(User user);
    List<User> getAllUsers();
    User getUserById(int id);
    //void updateUser(User user);
    boolean deleteUser(int id);
}