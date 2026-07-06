package com.reqai.backend.service;

import com.reqai.backend.entity.OutboxEvent;
import com.reqai.backend.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.reqai.backend.entity.OutboxStatus.PROCESSED;

@Service
@Slf4j
@RequiredArgsConstructor
public class OutboxPollerService {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String,String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processOutboxEvent(){

        // pull first 10 message (it uses skip locked strategy)
        List<OutboxEvent> events = outboxEventRepository.findPendingEventsForProcessing();

        if (events.isEmpty()){
            return; // if no data exist for processing then exit
        }
        log.info("{} outbox message being proccessed..." + events.size());

        for (OutboxEvent event : events){
            try{
                // send to kafka and wait for response
                kafkaTemplate.send("document-analysis-topic",event.getDocumentId().toString()).get();

                // update the status if it's okay
                event.setStatus(PROCESSED);
            }catch (Exception e){
                log.error("Event could not send to kafka! ID {}",event.getId(),e);
                // if error exist then rollback
                throw new RuntimeException("Kafka transmission error, operation being rolled back",e);
            }
        }
    }
}
