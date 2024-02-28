package com.lsalmeida.springsse.domain;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseUsecase {

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public void addEmitter(SseEmitter emitter) {
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
    }

    @Scheduled(fixedRate = 2000)
    public void sendEvents() {
        emitters.forEach(e ->
                {
                    try {
                        e.send(System.currentTimeMillis());
                    } catch (IOException ex) {
                        e.complete();
                        emitters.remove(e);
                        throw new RuntimeException(ex);
                    }
                }
        );
    }

}
