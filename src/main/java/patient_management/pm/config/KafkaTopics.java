package patient_management.pm.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopics {

    @Bean
    public NewTopic patientTopic(){
        return TopicBuilder.name("patient")
                .partitions(2)
                .replicas(1)
                .build();
    }

}
