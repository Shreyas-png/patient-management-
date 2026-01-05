package patient_management.pm.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import patient_management.pm.entity.Patient;
import patient_management.pm.proto.BillingRequest;
import patient_management.pm.proto.BillingResponse;
import patient_management.pm.proto.BillingServiceGrpc;

@Service
public class BillingServiceGrpcClient {

    @GrpcClient("billingService-client")
    private BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    public BillingResponse addPatientToBillingService(Patient savedPatient, String status){

        //Building GRPC Request for billing account
        BillingRequest billingRequest = BillingRequest.newBuilder()
                .setEmail(savedPatient.getEmail())
                .setPatientId(savedPatient.getId().toString())
                .setStatus(status)
                .build();

        return blockingStub.createBillingAccount(billingRequest);
    }
}
