package com.reqai.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class SseService {

    // The safe list we keep costumer id's and their sse pipelines
    private final Map<String, SseEmitter> emitters  = new ConcurrentHashMap<String, SseEmitter>();
    private final Map<String, Object> pendingEvents = new ConcurrentHashMap<String, Object>();

         // This method will run when the frontend says, “I'm here, connect the pipeline”
        public SseEmitter createEmitter(String documentId){

            // we set timeout duration to 600.000 so the connection doesn't drop until AI finish the analysis
            SseEmitter emitter = new SseEmitter(600000L);

            emitters.put(documentId,emitter);

            // if connection drop or ends remove it from the map (this is critical to prevent memory leak)
            emitter.onCompletion(()->emitters.remove(documentId));
            emitter.onTimeout(()-> emitters.remove(documentId));
            emitter.onError((e) -> emitters.remove(documentId));

            // If an event arrived before the emitter was created, send it immediately
            if (pendingEvents.containsKey(documentId)) {
                try {
                    emitter.send(SseEmitter.event().name("analysis-result").data(pendingEvents.remove(documentId)));
                    emitter.complete();
                } catch (IOException e) {
                    emitters.remove(documentId);
                }
            }

            return emitter;
        }

        public void sendEvent(String documentId, Object eventData){
            SseEmitter emitter = emitters.get(documentId);
            if (emitter != null){
                try {
                    emitter.send(SseEmitter.event().name("analysis-result").data(eventData));
                    emitter.complete();
                }catch (IOException e){
                    emitters.remove(documentId);
                }
            } else {
                // Emitter not yet connected, store it temporarily
                pendingEvents.put(documentId, eventData);
            }
    }
}
