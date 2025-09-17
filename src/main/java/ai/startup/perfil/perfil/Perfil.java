package ai.startup.perfil.perfil;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
@Document("perfis")
public class Perfil {
    @Id
    private String id;

    private String userId;    // user_id
    private String topic;
    private String subskill;

    private Long acertos;
    private Long erros;
    private Double acuracia;  // ex.: 0.85 = 85%
}