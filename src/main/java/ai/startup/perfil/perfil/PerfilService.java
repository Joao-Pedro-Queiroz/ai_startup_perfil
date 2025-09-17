package ai.startup.perfil.perfil;


import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PerfilService {

    private final PerfilRepository repo;

    public PerfilService(PerfilRepository repo) {
        this.repo = repo;
    }

    // POST em lote
    public List<PerfilDTO> criarEmLote(List<PerfilCreateDTO> itens) {
        var entidades = itens.stream().map(this::fromCreate).toList();
        return repo.saveAll(entidades).stream().map(this::toDTO).toList();
    }

    public List<PerfilDTO> listar() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    public List<PerfilDTO> listarPorUsuario(String userId) {
        return repo.findByUserId(userId).stream().map(this::toDTO).toList();
    }

    public List<PerfilDTO> listarPorUsuarioETopic(String userId, String topic) {
        return repo.findByUserIdAndTopic(userId, topic).stream().map(this::toDTO).toList();
    }

    public PerfilDTO obter(String id) {
        return repo.findById(id).map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Perfil não encontrado."));
    }

    // update parcial
    public PerfilDTO atualizar(String id, PerfilUpdateDTO dto) {
        var p = repo.findById(id).orElseThrow(() -> new RuntimeException("Perfil não encontrado."));

        if (dto.user_id() != null) p.setUserId(dto.user_id());
        if (dto.topic()   != null) p.setTopic(dto.topic());
        if (dto.subskill()!= null) p.setSubskill(dto.subskill());
        if (dto.acertos() != null) p.setAcertos(dto.acertos());
        if (dto.erros()   != null) p.setErros(dto.erros());
        if (dto.acuracia()!= null) p.setAcuracia(dto.acuracia());

        return toDTO(repo.save(p));
    }

    public void deletar(String id) {
        if (!repo.existsById(id)) throw new RuntimeException("Perfil não encontrado.");
        repo.deleteById(id);
    }

    // mappers
    private Perfil fromCreate(PerfilCreateDTO d) {
        return Perfil.builder()
                .userId(d.user_id())
                .topic(d.topic())
                .subskill(d.subskill())
                .acertos(d.acertos())
                .erros(d.erros())
                .acuracia(d.acuracia())
                .build();
    }

    private PerfilDTO toDTO(Perfil p) {
        return new PerfilDTO(
                p.getId(),
                p.getUserId(),
                p.getTopic(),
                p.getSubskill(),
                p.getAcertos(),
                p.getErros(),
                p.getAcuracia()
        );
    }
}