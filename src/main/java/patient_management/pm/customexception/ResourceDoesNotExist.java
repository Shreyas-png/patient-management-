package patient_management.pm.customexception;

public class ResourceDoesNotExist extends RuntimeException {
    public ResourceDoesNotExist(String message) {
        super(message);
    }
}
