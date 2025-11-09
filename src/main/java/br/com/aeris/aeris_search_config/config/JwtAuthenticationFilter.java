package br.com.aeris.aeris_search_config.config;

import br.com.aeris.aeris_search_config.model.Usuario;
import br.com.aeris.aeris_search_config.repository.UsuarioRepository;
import br.com.aeris.aeris_search_config.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = getJwtFromRequest(request);

            logger.info("JWT recebido: " + (jwt != null ? "Sim" : "Não"));

            if (StringUtils.hasText(jwt)) {
                logger.info("Validando token...");

                if (jwtUtil.validateToken(jwt)) {
                    Usuario usuario = usuarioRepository.findByEmail(jwtUtil.extractEmail(jwt));
                    boolean isAdmin = "adm".equals(jwtUtil.extractTipo(jwt));

                    logger.info("Token válido! UserId: " + usuario.getId() + ", IsAdmin: " + isAdmin);

                    List<SimpleGrantedAuthority> authorities = isAdmin
                            ? Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                            : Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(usuario.getId(), null, authorities);

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    logger.error("Token inválido ou expirado!");
                }
            }
        } catch (Exception ex) {
            logger.error("Erro ao validar token: ", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

