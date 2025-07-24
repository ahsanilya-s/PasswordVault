import db.DatabaseHelper;
import security.MasterPasswordManager;
import ui.LoginScreen;

public class Main {
    public static void main(String[] args) {
        DatabaseHelper.createTables();

        if (!MasterPasswordManager.isMasterSet()) {
            MasterPasswordManager.createMasterPassword(); // still uses console
        }

        new LoginScreen(); // start GUI login
    }
}
