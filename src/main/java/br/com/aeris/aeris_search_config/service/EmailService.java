package br.com.aeris.aeris_search_config.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Async
    public void enviarEmailHtml(String destinatario, String assunto,
                                String template, Map<String, Object> variaveis) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(assunto);

            Context context = new Context();
            context.setVariables(variaveis);
            String conteudoHtml = templateEngine.process(template, context);

            helper.setText(conteudoHtml, true);

            mailSender.send(message);
            log.info("Email enviado com sucesso para: {}", destinatario);

        } catch (MessagingException e) {
            log.error("Erro ao enviar email para: {}", destinatario, e);
            throw new RuntimeException("Falha no envio do email", e);
        }
    }

    public void enviarEmailNovaPesquisa(String destinatario, String nome, String empresa, String chave) {
        Map<String, Object> variaveis = Map.of(
                "nome", nome,
                "empresa", empresa,
                "chave", chave,
                "email", destinatario
        );
        enviarEmailHtml(destinatario, "Responda a sua nova pesquisa", "email-nova-pesquisa", variaveis);
    }
}

