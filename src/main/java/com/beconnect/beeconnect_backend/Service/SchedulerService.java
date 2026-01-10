package com.beconnect.beeconnect_backend.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SchedulerService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Uruchamia się codziennie o północy (00:00:00)
     * Cron expression: sekunda minuta godzina dzień miesiąc dzień_tygodnia
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void runDailyDatabaseCleanup() {
        System.out.println("--- [JOB START] Uruchamianie procedury czyszczenia obszarów: " + LocalDateTime.now());

        try {
            jdbcTemplate.execute("EXEC sp_DeactivateExpiredAreas");

            System.out.println("--- [JOB END] Procedura wykonana pomyślnie.");
        } catch (Exception e) {
            System.err.println("!!! [JOB ERROR] Błąd podczas wykonywania procedury: " + e.getMessage());
        }
    }
}