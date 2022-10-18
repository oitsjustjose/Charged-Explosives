package com.oitsjustjose.charged_explosives.common;

import com.google.common.collect.Sets;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Set;
import java.util.UUID;

public class TickScheduler {
    protected Set<ScheduledTask> tasks = Sets.newConcurrentHashSet();


    @SubscribeEvent
    public void registerEvent(TickEvent.ServerTickEvent event) {
        long now = System.currentTimeMillis();
        this.tasks.removeIf(x -> x.ready(now));
    }

    public UUID addTask(ScheduledTask task) {
        this.tasks.add(task);
        return task.getUuid();
    }

    public void cancelTask(UUID uuid) {
        this.tasks.removeIf(x -> x.uuid.equals(uuid));
    }

    public static class ScheduledTask {
        public final Runnable exec;
        public final long startTime;
        public final int delayTimeSeconds;
        private final UUID uuid;

        public ScheduledTask(Runnable exec, int delay) {
            this.exec = exec;
            this.delayTimeSeconds = delay;
            this.startTime = System.currentTimeMillis();
            this.uuid = UUID.randomUUID();
        }

        public boolean ready(long time) {
            boolean ready = time - startTime >= (delayTimeSeconds * 1000L);
            if (ready) this.exec.run();
            return ready;
        }

        public UUID getUuid() {
            return this.uuid;
        }
    }
}
