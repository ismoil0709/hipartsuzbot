package uz.hiparts.hipartsuz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uz.hiparts.hipartsuz.model.BotSettings;

@Repository
public interface BotSettingsRepository extends JpaRepository<BotSettings, Long> {
}
