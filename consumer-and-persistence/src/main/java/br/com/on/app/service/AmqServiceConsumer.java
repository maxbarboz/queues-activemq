package br.com.on.app.service;

import br.com.on.app.exception.MyPersonalException;
import br.com.on.app.component.PessoaComponent;
import br.com.on.app.config.ActiveAMQProperties;
import br.com.on.app.entity.PessoaEntity;
import br.com.on.app.util.JsonDesconverter;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.*;
import java.util.Objects;

@Service
@Log4j2
public class AmqServiceConsumer {

    private final PessoaComponent pessoaComponent;
    private final ActiveAMQProperties activeAMQProperties;

    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    @Autowired
    public AmqServiceConsumer(PessoaComponent pessoaComponent, ActiveAMQProperties activeAMQProperties) {
        this.pessoaComponent = pessoaComponent;
        this.activeAMQProperties = activeAMQProperties;
    }

    @PostConstruct
    public void startConsumer() {
        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(activeAMQProperties.getBrokerUrl());

            connection = connectionFactory.createConnection(activeAMQProperties.getUser(), activeAMQProperties.getPassword());
            connection.start();

            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(activeAMQProperties.getNameQueue());

            consumer = session.createConsumer(destination);

            consumer.setMessageListener(message -> {
                try {
                    if (message instanceof TextMessage) {
                        String messagePessoa = ((TextMessage) message).getText();
                        PessoaEntity pessoaEntity = JsonDesconverter.desconverterJson(messagePessoa);

                        if (pessoaComponent.isPessoaNaoCadastrada(pessoaEntity)) {
                            message.acknowledge();
                            log.info("Pessoa já cadastrada na base de dados - CPF {}", pessoaEntity.getCpf());
                        } else {
                            pessoaComponent.save(pessoaEntity);
                            message.acknowledge();
                        }
                    } else {
                        throw new MyPersonalException("Erro ao ler mensagem da fila!");
                    }
                } catch (MyPersonalException e) {
                    log.error("Erro de negócio: {}", e.getMessage());
                } catch (Exception e) {
                    log.error("Erro inesperado: {}", e.getMessage());
                }
            });

            log.info("Consumidor conectado e aguardando mensagens na fila: {}", activeAMQProperties.getNameQueue());
        } catch (JMSException e) {
            throw new MyPersonalException("Erro ao iniciar consumidor: ", e);
        }
    }

    @PreDestroy
    public void shutdown() {
        try {
            if (Objects.nonNull(consumer)) consumer.close();
            if (Objects.nonNull(session)) session.close();
            if (Objects.nonNull(connection)) connection.close();
            log.warn("Consumidor desconectado");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
