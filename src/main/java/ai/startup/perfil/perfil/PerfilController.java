package ai.startup.perfil.perfil;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@SecurityRequirement(name = "bearerAuth")
public class PerfilController {

    private final PerfilService service;

    public PerfilController(PerfilService service) {
        this.service = service;
    }

    @PostMapping("/perfis")
    public ResponseEntity<PerfilDTO> criarOuAtualizar(@RequestBody PerfilCreateDTO dto) {
        return ResponseEntity.ok(service.criarOuAtualizar(dto));
    }

    @GetMapping("/perfis")
    public ResponseEntity<List<PerfilDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/perfis/{id}")
    public ResponseEntity<PerfilDTO> obter(@PathVariable String id) {
        return ResponseEntity.ok(service.obter(id));
    }

    @PutMapping("/perfis/{id}")
    public ResponseEntity<PerfilDTO> atualizar(@PathVariable String id, @RequestBody PerfilUpdateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/perfis/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/perfis/by-usuario/{userId}")
    public ResponseEntity<List<PerfilDTO>> listarPorUsuario(@PathVariable String userId) {
        return ResponseEntity.ok(service.listarPorUsuario(userId));
    }

    @GetMapping("/perfis/by-usuario-topic")
    public ResponseEntity<PerfilDTO> listarPorUsuarioETopic(@RequestParam("user_id") String userId,
                                                            @RequestParam String topic) {
        return ResponseEntity.ok(service.listarPorUsuarioETopic(userId, topic));
    }
}