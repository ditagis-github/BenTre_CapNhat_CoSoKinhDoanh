package bentre.ditagis.com.capnhatthongtin.entities.entitiesDB;

public class User {
    private String userName;
    private String passWord;
    private String displayName;
    private boolean isQuan5;
    private boolean isQuan6;
    private boolean isQuan8;
    private boolean isQuanBinhTan;

    private boolean isCreate;
    private boolean isValid;
    public static User userDangNhap;

    public User() {

    }

    public User(String userName, String passWord, String displayName, boolean isQuan5, boolean isQuan6, boolean isQuan8, boolean isQuanBinhTan, boolean isCreate, boolean isValid) {
        this.userName = userName;
        this.passWord = passWord;
        this.displayName = displayName;
        this.isQuan5 = isQuan5;
        this.isQuan6 = isQuan6;
        this.isQuan8 = isQuan8;
        this.isQuanBinhTan = isQuanBinhTan;
        this.isCreate = isCreate;
        this.isValid = isValid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isQuan5() {
        return isQuan5;
    }

    public void setQuan5(boolean quan5) {
        isQuan5 = quan5;
    }

    public boolean isQuan6() {
        return isQuan6;
    }

    public void setQuan6(boolean quan6) {
        isQuan6 = quan6;
    }

    public boolean isQuan8() {
        return isQuan8;
    }

    public void setQuan8(boolean quan8) {
        isQuan8 = quan8;
    }

    public boolean isQuanBinhTan() {
        return isQuanBinhTan;
    }

    public void setQuanBinhTan(boolean quanBinhTan) {
        isQuanBinhTan = quanBinhTan;
    }


    public boolean isCreate() {
        return isCreate;
    }

    public void setCreate(boolean create) {
        isCreate = create;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}