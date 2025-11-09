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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Async
    public void enviarEmailHtml(String destinatario, String assunto,
                                String template, Map<String, Object> variaveis) {
        log.info("[EmailService.enviarEmailHtml] Iniciando envio de email HTML para: {} | Assunto: {} | Template: {}", destinatario, assunto, template);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(assunto);

            Context context = new Context();
            context.setVariables(variaveis);
            log.debug("[EmailService.enviarEmailHtml] Variáveis do template: {}", variaveis);

            String conteudoHtml = templateEngine.process(template, context);
            log.debug("[EmailService.enviarEmailHtml] Template processado com sucesso para destinatário: {}", destinatario);

            helper.setText(conteudoHtml, true);

            mailSender.send(message);
            log.info("[EmailService.enviarEmailHtml] Email enviado com sucesso para: {}", destinatario);

        } catch (MessagingException e) {
            log.error("[EmailService.enviarEmailHtml] Erro ao enviar email para: {} | Assunto: {}", destinatario, assunto, e);
            throw new RuntimeException("Falha no envio do email", e);
        }
    }

    public void enviarEmailNovaPesquisa(String destinatario, String nome, String empresa, String chave) {
        log.info("[EmailService.enviarEmailNovaPesquisa] Preparando envio de email de nova pesquisa para: {} | Empresa: {}", destinatario, empresa);

        Map<String, Object> variaveis = Map.of(
                "nome", nome,
                "empresa", empresa,
                "chave", chave,
                "email", destinatario
        );
        log.debug("[EmailService.enviarEmailNovaPesquisa] Variáveis do template: {}", variaveis);

        enviarEmailHtml(destinatario, "Responda a sua nova pesquisa", "email-nova-pesquisa", variaveis);

        log.info("[EmailService.enviarEmailNovaPesquisa] Email de nova pesquisa enviado para: {}", destinatario);
    }

}

