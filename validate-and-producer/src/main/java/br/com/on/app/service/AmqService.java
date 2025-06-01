package br.com.on.app.service;

import br.com.on.app.config.ActiveMQProperties;
import br.com.on.app.dto.PessoaValidationDTO;
import br.com.on.app.util.JsonConverter;
import br.com.on.app.validators.MyPessoaValidators;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

@Log4j2
@Getter
@Setter
@Service
public class AmqService {

    private ActiveMQProperties activeMQProperties;

    @Autowired
    private AmqService(ActiveMQProperties activeMQProperties) {
        this.activeMQProperties = activeMQProperties;
    }

    public void validarPessoaAndEnviar(PessoaValidationDTO pessoa){
        log.info("Iniciando validação de registro da pessoa - CPF {}", pessoa.getCpf());
        MyPessoaValidators.of(pessoa.getCpf(), pessoa.getEmail(), pessoa.getTelefone())
                .isCPFValido()
                .isValidEmail()
                .isTelefoneValido();

        log.info("Registro validado com sucesso, enviando para a fila.");
        enviarParaFila(pessoa);
    }

    private void enviarParaFila(PessoaValidationDTO pessoa) {
        try (ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(activeMQProperties.getBrokerUrl());
             Connection connection = factory.createConnection(activeMQProperties.getUser(), activeMQProperties.getPassword());
             Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)) {

            connection.start();

            Queue queue = session.createQueue(activeMQProperties.getNameQueue());
            MessageProducer producer = session.createProducer(queue);

            TextMessage message = session.createTextMessage(JsonConverter.converterJson(pessoa));
            message.setStringProperty("JMS_AMQ_DUPLICATE_DETECTION_ID", pessoa.getCpf());
            producer.send(message);

            System.out.println("Registro incluído na fila para processamento");

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }



}
