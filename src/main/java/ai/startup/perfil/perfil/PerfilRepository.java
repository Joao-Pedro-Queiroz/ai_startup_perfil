package ai.startup.perfil.perfil;

import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PerfilRepository extends MongoRepository<Perfil, String> {
    List<Perfil> findByUserId(String userId);
    List<Perfil> findByUserIdAndTopic(String userId, String topic); // <- novo
}