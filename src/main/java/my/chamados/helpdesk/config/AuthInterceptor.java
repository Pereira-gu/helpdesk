package my.chamados.helpdesk.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("usuarioLogado") != null) {
            return true; // Usuário está logado, continua com a requisição
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
            return false; // Usuário não está logado, redireciona para o login
        }
    }
}