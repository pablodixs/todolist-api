package com.pablodias.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.pablodias.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var servletPath = request.getServletPath();

        if (servletPath.equals("/tasks")) {
            var auth = request.getHeader("Authorization");

            var encodedPassword = auth.substring("Basic".length()).trim();

            byte[] passwordEncoded = Base64.getDecoder()
                    .decode(encodedPassword);

            var decodedPassword = new String(passwordEncoded);
            String[] credentials = decodedPassword.split(":");

            var username = credentials[0];
            var password = credentials[1];

            // Validar usuário
            var user = this.userRepository.findByUsername(username);

            if (user == null) {
                response.sendError(401);
            } else {
                var verifiedPassword = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());

                if (verifiedPassword.verified) {
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
