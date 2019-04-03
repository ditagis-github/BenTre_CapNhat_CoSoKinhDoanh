package bentre.ditagis.com.capnhatthongtin.entities.entitiesDB;

public class UserDangNhap {
    private User user;

    private UserDangNhap() {

    }

    private static UserDangNhap instance = null;

    public static UserDangNhap getInstance() {
        if (instance == null) {
            instance = new UserDangNhap();
        }
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static void setInstance(UserDangNhap instance) {
        UserDangNhap.instance = instance;
    }
}
