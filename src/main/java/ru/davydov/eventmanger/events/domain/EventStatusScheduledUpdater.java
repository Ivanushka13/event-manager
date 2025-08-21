package ru.davydov.eventmanger.events.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.davydov.eventmanger.events.db.EventRepository;

@EnableScheduling
@Configuration
@Slf4j
@RequiredArgsConstructor
public class EventStatusScheduledUpdater {

    private final EventRepository eventRepository;

    @Scheduled(cron = "${event.stats.cron}")
    public void updateEventStatus() {
        log.info("EventStatusScheduledUpdater started");

        var startedEvents = eventRepository.findStartedEventsWithStatus(EventStatus.WAIT_START);
        startedEvents.forEach(eventId ->
                eventRepository.changeEventStatus(eventId, EventStatus.STARTED)
        );

        var endedEvents = eventRepository.findEndedEventsWithStatus(EventStatus.STARTED);
        endedEvents.forEach(eventId ->
                eventRepository.changeEventStatus(eventId, EventStatus.FINISHED)
        );
    }
}
