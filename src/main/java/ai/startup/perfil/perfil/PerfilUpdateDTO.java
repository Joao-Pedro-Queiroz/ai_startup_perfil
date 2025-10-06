package ai.startup.perfil.perfil;

import java.util.Map;

/** Update parcial: se 'topics' vier, fazemos merge (por tópico/subskill/structure) */
public record PerfilUpdateDTO(
        String user_id,
        Map<String, TopicDTO> topics
) {}