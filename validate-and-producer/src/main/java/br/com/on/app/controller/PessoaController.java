package br.com.on.app.controller;

import br.com.on.app.dto.PessoaValidationDTO;
import br.com.on.app.service.AmqServiceProducer;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pessoas")
@Log4j2
@CrossOrigin
public class PessoaController {

    private final AmqServiceProducer service;

    @Autowired
    public PessoaController(AmqServiceProducer service) {
        this.service = service;
    }

    @PostMapping("/validation")
    public ResponseEntity<PessoaValidationDTO> create(@Valid @RequestBody PessoaValidationDTO pessoaValidationDTO) {
        log.debug("Iniciando processo de validação e envio da pessoa para a fila");
        service.validarPessoaAndEnviar(pessoaValidationDTO);
        log.debug("Processo de validação e envio da pessoa para a fila CONCLUÍDO");
        return ResponseEntity.ok(null);
    }

}
