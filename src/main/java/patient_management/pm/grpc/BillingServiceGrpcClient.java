package patient_management.pm.grpc;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import patient_management.pm.proto.BillingRequest;
import patient_management.pm.proto.BillingResponse;
import patient_management.pm.proto.BillingServiceGrpc;

@Service
public class BillingServiceGrpcClient {

    @GrpcClient("billingService-client")
    private BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    public BillingResponse addPatientToBillingService(BillingRequest billingRequest){
        return blockingStub.createBillingAccount(billingRequest);
    }
}
