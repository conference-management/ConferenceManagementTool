package user;

import java.util.List;

public interface AdminManagement {

    void addAdmin(Admin a);

    List<Admin> getAllAdmins();

    Admin getAdminPersonalData(int ID);

    void removeAdmin(int ID);

    void logoutAdmin(int ID);

    void editAdmin(int ID, Admin a);

    void deleteAllAdmins();

}
