package br.com.on.app.util;

import br.com.on.app.entity.PessoaEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class JsonDesconverter {

    private JsonDesconverter() {
        //vazio
    }

    public static PessoaEntity desconverterJson(String messagePessoa) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());

        try {
            return mapper.readValue(messagePessoa, PessoaEntity.class);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter JSON para Pessoa", e);
        }
    }
}
