package ai.startup.perfil.perfil;

public record PerfilDTO(
        String id,
        String user_id,
        String topic,
        String subskill,
        Long acertos,
        Long erros,
        Double acuracia
) {}