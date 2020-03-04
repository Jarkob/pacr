package pacr.webapp_backend.git_tracking.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Provides a TaskScheduler for PullIntervalScheduler.
 *
 * @author Pavel Zwerschke
 */
@Configuration
public class PullIntervalSchedulerConfiguration {

    /**
     * TaskScheduler for PullIntervalScheduler.
     * @return TaskScheduler.
     */
    @Bean
    public TaskScheduler poolScheduler() {
        final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("ThreadPoolTaskScheduler");
        scheduler.setPoolSize(1);
        scheduler.initialize();
        return scheduler;
    }

}
