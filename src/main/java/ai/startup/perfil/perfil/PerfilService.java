package ai.startup.perfil.perfil;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PerfilService {

    private final PerfilRepository repo;

    public PerfilService(PerfilRepository repo) {
        this.repo = repo;
    }

    // ===== CRUD =====
    public List<PerfilDTO> listar() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    public PerfilDTO obter(String id) {
        return repo.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado."));
    }

    /** POST (singular): cria ou atualiza por user_id com merge hierárquico */
    public PerfilDTO criarOuAtualizar(PerfilCreateDTO it) {
        if (it == null || it.user_id() == null) {
            throw new RuntimeException("Payload inválido: user_id é obrigatório.");
        }

        Perfil p = repo.findFirstByUserId(it.user_id());
        if (p == null) {
            p = Perfil.builder()
                    .userId(it.user_id())
                    .topics(fromTopicsDTO(it.topics()))
                    .build();
        } else {
            if (p.getTopics() == null) p.setTopics(new HashMap<>());
            mergeTopics(p.getTopics(), fromTopicsDTO(it.topics()));
        }
        return toDTO(repo.save(p));
    }

    /** Update parcial por id: merge recursivo de topics/subskills/structures */
    public PerfilDTO atualizar(String id, PerfilUpdateDTO dto) {
        var p = repo.findById(id).orElseThrow(() -> new RuntimeException("Perfil não encontrado."));
        if (dto.user_id() != null) p.setUserId(dto.user_id());
        if (dto.topics() != null) {
            if (p.getTopics() == null) p.setTopics(new HashMap<>());
            mergeTopics(p.getTopics(), fromTopicsDTO(dto.topics()));
        }
        return toDTO(repo.save(p));
    }

    public void deletar(String id) {
        if (!repo.existsById(id)) throw new RuntimeException("Perfil não encontrado.");
        repo.deleteById(id);
    }

    public List<PerfilDTO> listarPorUsuario(String userId) {
        return repo.findByUserId(userId).stream().map(this::toDTO).toList();
    }

    /** Recorte por tópico do usuário (útil para painéis) */
    public PerfilDTO listarPorUsuarioETopic(String userId, String topic) {
        var p = repo.findFirstByUserId(userId);
        if (p == null) throw new RuntimeException("Perfil não encontrado para o usuário.");
        Map<String, Perfil.TopicProfile> slice = new HashMap<>();
        if (p.getTopics() != null && p.getTopics().containsKey(topic)) {
            slice.put(topic, p.getTopics().get(topic));
        }
        return new PerfilDTO(p.getId(), p.getUserId(), toTopicsDTO(slice));
    }

    // ======= Mappers DTO <-> Entidade =======
    private Map<String, Perfil.TopicProfile> fromTopicsDTO(Map<String, TopicDTO> dtos) {
        if (dtos == null) return null;
        Map<String, Perfil.TopicProfile> out = new HashMap<>();
        dtos.forEach((topic, t) -> out.put(topic, fromTopicDTO(t)));
        return out;
    }

    private Perfil.TopicProfile fromTopicDTO(TopicDTO dto) {
        if (dto == null) return null;
        Map<String, Perfil.SubskillProfile> subs = new HashMap<>();
        if (dto.subskills() != null) {
            dto.subskills().forEach((sub, s) -> subs.put(sub, fromSubskillDTO(s)));
        }
        return Perfil.TopicProfile.builder().subskills(subs).build();
    }

    private Perfil.SubskillProfile fromSubskillDTO(SubskillDTO s) {
        if (s == null) return null;
        Map<String, Perfil.StructureProfile> structs = new HashMap<>();
        if (s.structures() != null) {
            s.structures().forEach((name, st) -> structs.put(name, fromStructureDTO(st)));
        }
        return Perfil.SubskillProfile.builder()
                .attempts_s(s.attempts_s())
                .correct_s(s.correct_s())
                .hints_rate_s(s.hints_rate_s())
                .solutions_rate_s(s.solutions_rate_s())
                .last_seen_at_s(s.last_seen_at_s())
                .missed_two_sessions(s.missed_two_sessions())
                .easy_seen_s(s.easy_seen_s())
                .medium_seen_s(s.medium_seen_s())
                .hard_seen_s(s.hard_seen_s())
                .estruturas_vistas_s(s.estruturas_vistas_s())
                .total_estruturas_s(s.total_estruturas_s())
                .structures(structs)
                .build();
    }

    private Perfil.StructureProfile fromStructureDTO(StructureDTO st) {
        if (st == null) return null;
        return Perfil.StructureProfile.builder()
                .P_sc(st.P_sc())
                .attempts_sc(st.attempts_sc())
                .correct_sc(st.correct_sc())
                .hints_rate_sc(st.hints_rate_sc())
                .solutions_rate_sc(st.solutions_rate_sc())
                .easy_seen_sc(st.easy_seen_sc())
                .medium_seen_sc(st.medium_seen_sc())
                .hard_seen_sc(st.hard_seen_sc())
                .medium_exposures_sc(st.medium_exposures_sc())
                .hard_exposures_sc(st.hard_exposures_sc())
                .last_level_applied_sc(st.last_level_applied_sc())
                .cooldown_sc(st.cooldown_sc())
                .last_seen_at_sc(st.last_seen_at_sc())
                .build();
    }

    private Map<String, TopicDTO> toTopicsDTO(Map<String, Perfil.TopicProfile> map) {
        if (map == null) return null;
        Map<String, TopicDTO> out = new HashMap<>();
        map.forEach((topic, t) -> out.put(topic, toTopicDTO(t)));
        return out;
    }

    private TopicDTO toTopicDTO(Perfil.TopicProfile t) {
        if (t == null) return null;
        Map<String, SubskillDTO> subs = new HashMap<>();
        if (t.getSubskills() != null) {
            t.getSubskills().forEach((sub, s) -> subs.put(sub, toSubskillDTO(s)));
        }
        return new TopicDTO(subs);
    }

    private SubskillDTO toSubskillDTO(Perfil.SubskillProfile s) {
        Map<String, StructureDTO> structs = new HashMap<>();
        if (s.getStructures() != null) {
            s.getStructures().forEach((name, st) -> structs.put(name, toStructureDTO(st)));
        }
        return new SubskillDTO(
                s.getAttempts_s(),
                s.getCorrect_s(),
                s.getHints_rate_s(),
                s.getSolutions_rate_s(),
                s.getLast_seen_at_s(),
                s.getMissed_two_sessions(),
                s.getEasy_seen_s(),
                s.getMedium_seen_s(),
                s.getHard_seen_s(),
                s.getEstruturas_vistas_s(),
                s.getTotal_estruturas_s(),
                structs
        );
    }

    private StructureDTO toStructureDTO(Perfil.StructureProfile st) {
        return new StructureDTO(
                st.getP_sc(),
                st.getAttempts_sc(),
                st.getCorrect_sc(),
                st.getHints_rate_sc(),
                st.getSolutions_rate_sc(),
                st.getEasy_seen_sc(),
                st.getMedium_seen_sc(),
                st.getHard_seen_sc(),
                st.getMedium_exposures_sc(),
                st.getHard_exposures_sc(),
                st.getLast_level_applied_sc(),
                st.getCooldown_sc(),
                st.getLast_seen_at_sc()
        );
    }

    private PerfilDTO toDTO(Perfil p) {
        return new PerfilDTO(p.getId(), p.getUserId(), toTopicsDTO(p.getTopics()));
    }

    // ===== Merge recursivo (só sobrescreve campos não nulos) =====
    private void mergeTopics(Map<String, Perfil.TopicProfile> base, Map<String, Perfil.TopicProfile> inc) {
        if (inc == null || base == null) return;
        for (var eTopic : inc.entrySet()) {
            var topic = eTopic.getKey();
            var incTopic = eTopic.getValue();
            var baseTopic = base.computeIfAbsent(topic,
                    t -> Perfil.TopicProfile.builder().subskills(new HashMap<>()).build());
            mergeSubskills(baseTopic.getSubskills(), incTopic.getSubskills());
        }
    }

    private void mergeSubskills(Map<String, Perfil.SubskillProfile> base, Map<String, Perfil.SubskillProfile> inc) {
        if (inc == null || base == null) return;
        for (var eSub : inc.entrySet()) {
            var key = eSub.getKey();
            var in = eSub.getValue();
            var b = base.computeIfAbsent(key, k -> Perfil.SubskillProfile.builder().build());

            if (in.getAttempts_s() != null) b.setAttempts_s(in.getAttempts_s());
            if (in.getCorrect_s()  != null) b.setCorrect_s(in.getCorrect_s());
            if (in.getHints_rate_s()!= null) b.setHints_rate_s(in.getHints_rate_s());
            if (in.getSolutions_rate_s()!= null) b.setSolutions_rate_s(in.getSolutions_rate_s());
            if (in.getLast_seen_at_s()!= null) b.setLast_seen_at_s(in.getLast_seen_at_s());
            if (in.getMissed_two_sessions() != null) b.setMissed_two_sessions(in.getMissed_two_sessions());
            if (in.getEasy_seen_s() != null) b.setEasy_seen_s(in.getEasy_seen_s());
            if (in.getMedium_seen_s()!= null) b.setMedium_seen_s(in.getMedium_seen_s());
            if (in.getHard_seen_s()  != null) b.setHard_seen_s(in.getHard_seen_s());
            if (in.getEstruturas_vistas_s()!= null) b.setEstruturas_vistas_s(in.getEstruturas_vistas_s());
            if (in.getTotal_estruturas_s()!= null) b.setTotal_estruturas_s(in.getTotal_estruturas_s());

            if (b.getStructures() == null) b.setStructures(new HashMap<>());
            mergeStructures(b.getStructures(), in.getStructures());
        }
    }

    private void mergeStructures(Map<String, Perfil.StructureProfile> base, Map<String, Perfil.StructureProfile> inc) {
        if (inc == null || base == null) return;
        for (var e : inc.entrySet()) {
            var key = e.getKey();
            var in = e.getValue();
            var b = base.computeIfAbsent(key, k -> Perfil.StructureProfile.builder().build());

            if (in.getP_sc() != null) b.setP_sc(in.getP_sc());
            if (in.getAttempts_sc() != null) b.setAttempts_sc(in.getAttempts_sc());
            if (in.getCorrect_sc()  != null) b.setCorrect_sc(in.getCorrect_sc());
            if (in.getHints_rate_sc()!= null) b.setHints_rate_sc(in.getHints_rate_sc());
            if (in.getSolutions_rate_sc()!= null) b.setSolutions_rate_sc(in.getSolutions_rate_sc());
            if (in.getEasy_seen_sc()!= null) b.setEasy_seen_sc(in.getEasy_seen_sc());
            if (in.getMedium_seen_sc()!= null) b.setMedium_seen_sc(in.getMedium_seen_sc());
            if (in.getHard_seen_sc()!= null) b.setHard_seen_sc(in.getHard_seen_sc());
            if (in.getMedium_exposures_sc()!= null) b.setMedium_exposures_sc(in.getMedium_exposures_sc());
            if (in.getHard_exposures_sc()!= null) b.setHard_exposures_sc(in.getHard_exposures_sc());
            if (in.getLast_level_applied_sc()!= null) b.setLast_level_applied_sc(in.getLast_level_applied_sc());
            if (in.getCooldown_sc()!= null) b.setCooldown_sc(in.getCooldown_sc());
            if (in.getLast_seen_at_sc()!= null) b.setLast_seen_at_sc(in.getLast_seen_at_sc());
        }
    }
}
