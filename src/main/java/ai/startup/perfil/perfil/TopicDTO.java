package ai.startup.perfil.perfil;

import java.util.Map;

public record TopicDTO(
        Map<String, SubskillDTO> subskills
) {}
