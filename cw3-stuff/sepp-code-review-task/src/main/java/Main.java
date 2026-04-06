import com.fortytwogroup.controller.UserController;
import com.fortytwogroup.model.UserStorage;
import com.fortytwogroup.service.RegistrationUtility;
import com.fortytwogroup.view.TextUserInterface;
import com.fortytwogroup.service.PasswordService;
import com.fortytwogroup.model.User;

/**
 * Class containing main entry point for the lazy migration of Faculty Member use case
 * instantiation in system.
 */
public class Main {


  /**
   * Method acting as main entry point for the lazy migration of Faculty Member use case
   * @param args command line arguments accompanying java command in cli
   */
  public static void main(String[] args) {

    // Path to faculty csv for proof of concept purposes
    final String dummyFacultyFilePath = "src/main/resources/faculty.csv";

    // init thread objects
    PasswordService passwordService = new PasswordService();
    UserStorage userStorage = new UserStorage();
    RegistrationUtility registrationUtility = new RegistrationUtility(dummyFacultyFilePath);
    TextUserInterface textUserInterface = new TextUserInterface();

    // instantiate user controller for use in login method call
    UserController controller = new UserController(
        userStorage,
        textUserInterface,
        passwordService,
        registrationUtility);


    // initiate login use-case
    User currentUser = controller.login();



  }
}
