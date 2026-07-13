package my.chamados.helpdesk.controller;

import my.chamados.helpdesk.model.Chamado;
import my.chamados.helpdesk.repository.ChamadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chamados")
public class ChamadoController {

    @Autowired
    private ChamadoRepository chamadoRepository;

    // 1. Criar um chamado (Simulado: no futuro pegaremos o cliente logado no Spring Security)
    @PostMapping
    public Chamado criarChamado(@RequestBody Chamado chamado) {
        return chamadoRepository.save(chamado);
    }

    // 2. Listar TODOS os chamados do sistema
    @GetMapping
    public List<Chamado> listarTodos() {
        return chamadoRepository.findAll();
    }

    // 3. Rota da Fila do Técnico (Ordenado por urgência)
    @GetMapping("/fila")
    public List<Chamado> visualizarFila() {
        return chamadoRepository.buscarFilaDeAtendimento();
    }
}