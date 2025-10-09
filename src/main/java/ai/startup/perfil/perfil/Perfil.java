package ai.startup.perfil.perfil;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Document("UserProfile")
public class Perfil {
    @Id
    private String id;

    private String userId;                  // "user_id"
    private Map<String, TopicProfile> topics; // ex.: "algebra" -> TopicProfile

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TopicProfile {
        private Map<String, SubskillProfile> subskills; // ex.: "isolating_variables" -> SubskillProfile
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SubskillProfile {
        // campos *_s (nível subskill)
        private Long attempts_s;
        private Long correct_s;
        private Double hints_rate_s;
        private Double solutions_rate_s;
        private String last_seen_at_s; // pode ser ISO-8601; manter String p/ flexibilidade
        private Boolean easy_seen_s;
        private Boolean medium_seen_s;
        private Boolean hard_seen_s;
        private Long estruturas_vistas_s;
        private Long total_estruturas_s;

        private Map<String, StructureProfile> structures; // ex.: "one_step_isolating_variables" -> StructureProfile
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class StructureProfile {
        // campos *_sc (nível structure)
        private Integer P_sc;
        private Long attempts_sc;
        private Long correct_sc;
        private Double hints_rate_sc;
        private Double solutions_rate_sc;
        private Boolean easy_seen_sc;
        private Boolean medium_seen_sc;
        private Boolean hard_seen_sc;
        private Long medium_exposures_sc;
        private Long hard_exposures_sc;
        private String last_level_applied_sc; // easy/medium/hard
        private Integer cooldown_sc;
        private String last_seen_at_sc;
    }
}