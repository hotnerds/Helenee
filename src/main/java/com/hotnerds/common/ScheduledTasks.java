package com.hotnerds.common;

import com.hotnerds.fatsecret.FatSecretToken;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final FatSecretToken fatSecretToken;

    @Scheduled(cron = "* * */22 * * *")
    public void updateTokenTask() {
        fatSecretToken.updateToken();
    }
}
