package com.oitsjustjose.charged_explosives.common;

import com.google.common.collect.Sets;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Set;

public class TickScheduler {
    protected Set<ScheduledTask> tasks = Sets.newConcurrentHashSet();


    @SubscribeEvent
    public void registerEvent(TickEvent.ServerTickEvent event) {
        long now = System.currentTimeMillis();
        tasks.removeIf(x -> x.ready(now));
    }

    public void addTask(ScheduledTask task) {
        this.tasks.add(task);
    }

    public static class ScheduledTask {
        public final Runnable exec;
        public final long startTime;
        public final int delayTimeSeconds;

        public ScheduledTask(Runnable exec, int delay) {
            this.exec = exec;
            this.delayTimeSeconds = delay;
            this.startTime = System.currentTimeMillis();
        }

        public boolean ready(long time) {
            boolean ready = time - startTime >= (delayTimeSeconds * 1000L);
            if (ready) this.exec.run();
            return ready;
        }
    }
}
