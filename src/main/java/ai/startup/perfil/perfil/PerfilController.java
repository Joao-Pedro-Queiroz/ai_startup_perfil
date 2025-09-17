package ai.startup.perfil.perfil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class PerfilController {

    private final PerfilService service;

    public PerfilController(PerfilService service) {
        this.service = service;
    }

    // Protegidas (JWT): use o mesmo SecurityFilter/JwtService que nos outros serviços
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Criar perfis em lote")
    @PostMapping("/perfis")
    public ResponseEntity<List<PerfilDTO>> criarEmLote(@RequestBody List<PerfilCreateDTO> lista) {
        return ResponseEntity.ok(service.criarEmLote(lista));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/perfis")
    public ResponseEntity<List<PerfilDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

     // extra: listar por id_usuario
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/perfis/by-usuario/{userId}")
    public ResponseEntity<List<PerfilDTO>> listarPorUsuario(@PathVariable String userId) {
        return ResponseEntity.ok(service.listarPorUsuario(userId));
    }
    
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/perfis/by-usuario-topic")
    public ResponseEntity<List<PerfilDTO>> listarPorUsuarioETopic(
            @RequestParam("user_id") String userId,
            @RequestParam String topic) {
        return ResponseEntity.ok(service.listarPorUsuarioETopic(userId, topic));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/perfis/{id}")
    public ResponseEntity<PerfilDTO> obter(@PathVariable String id) {
        return ResponseEntity.ok(service.obter(id));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/perfis/{id}")
    public ResponseEntity<PerfilDTO> atualizar(@PathVariable String id, @RequestBody PerfilUpdateDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/perfis/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}