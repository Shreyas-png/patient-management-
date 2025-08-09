package patient_management.pm.customexception;

public class ResourceAlreadyExist extends RuntimeException{
    public ResourceAlreadyExist(String message){
        super(message);
    }
}
