package patient_management.pm.kafka;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient_management.pm.entity.Patient;
import patient_management.pm.proto.PatientEvent;

@Service
public class KafkaProducer {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, byte[]> kafkaTemplate;

     public KafkaProducer(KafkaTemplate<String, byte[]> kafkaTemplate){
         this.kafkaTemplate = kafkaTemplate;
     }

     public void pushPatientEvent(Patient patient, String event){

         //Building patient event object to push message to kafka
         PatientEvent patientEvent = PatientEvent.newBuilder()
                 .setPatientID(patient.getId().toString())
                 .setName(patient.getName())
                 .setEmail(patient.getEmail())
                 .setEventType(event)
                 .build();

         kafkaTemplate.send("patient",patient.getId().toString(), patientEvent.toByteArray())
                 .whenComplete((result, ex) -> {
                    if(ex == null){
                        RecordMetadata metadata = result.getRecordMetadata();
                        log.info("message Sent Successfully | Topic: {}, Partition: {}, offset: {}",
                                metadata.topic(),
                                metadata.partition(),
                                metadata.offset());
                    }else{
                        log.error("Exception Occurred While metadata Sending: {}", ex.getMessage());
                    }
                 });
     }
}
