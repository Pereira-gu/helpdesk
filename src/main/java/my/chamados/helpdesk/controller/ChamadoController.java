package my.chamados.helpdesk.controller;

import my.chamados.helpdesk.model.Chamado;
import my.chamados.helpdesk.model.HistoricoChamado;
import my.chamados.helpdesk.service.ChamadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/chamados")
public class ChamadoController {

    @Autowired
    private ChamadoService chamadoService;

    @PostMapping
    public ResponseEntity<Chamado> criarChamado(@RequestBody Chamado chamado) {
        try {
            Chamado novoChamado = chamadoService.abrirChamado(chamado);
            return ResponseEntity.ok(novoChamado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/fila")
    public List<Chamado> visualizarFila() {
        return chamadoService.listarFila();
    }

    @PutMapping("/{id}/assumir")
    public ResponseEntity<?> assumirChamado(@PathVariable Long id, @RequestParam Long tecnicoId) {
        try {
            Chamado chamadoAtendido = chamadoService.assumirChamado(id, tecnicoId);
            return ResponseEntity.ok(chamadoAtendido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Atualizado: Agora recebe a solução como parâmetro de texto (ou pode ser no corpo da requisição)
    // Ex: PUT /api/chamados/1/resolver?solucao=Foi resetado o modem de energia.
    @PutMapping("/{id}/resolver")
    public ResponseEntity<?> resolverChamado(@PathVariable Long id, @RequestParam String solucao) {
        try {
            Chamado chamadoResolvido = chamadoService.resolverChamado(id, solucao);
            return ResponseEntity.ok(chamadoResolvido);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint para adicionar um comentário qualquer durante o atendimento
    @PostMapping("/{id}/comentarios")
    public ResponseEntity<?> adicionarComentario(@PathVariable Long id, @RequestParam Long usuarioId, @RequestBody String texto) {
        try {
            HistoricoChamado comentario = chamadoService.adicionarComentario(id, usuarioId, texto);
            return ResponseEntity.ok(comentario);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Endpoint para o front-end desenhar a "linha do tempo" do chamado
    @GetMapping("/{id}/historico")
    public List<HistoricoChamado> verHistorico(@PathVariable Long id) {
        return chamadoService.listarHistoricoDoChamado(id);
    }
}