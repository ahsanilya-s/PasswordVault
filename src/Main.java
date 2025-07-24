import db.DatabaseHelper;
import security.MasterPasswordManager;

public class Main {
    public static void main(String[] args) {
        DatabaseHelper.createTables(); // setup DB

        if (!MasterPasswordManager.isMasterSet()) {
            MasterPasswordManager.createMasterPassword();
        } else {
            boolean verified = MasterPasswordManager.verifyMasterPassword();
            if (!verified) {
                System.out.println("❌ Incorrect master password. Exiting...");
                return;
            } else {
                System.out.println("✅ Access granted.");
            }
        }

        // Next: load dashboard or GUI
    }
}
