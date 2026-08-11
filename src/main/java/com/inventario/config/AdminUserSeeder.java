package com.inventario.config;

import com.inventario.model.RolNombre;
import com.inventario.model.Usuario;
import com.inventario.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AdminUserSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminUserSeeder.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminInitialPassword;

    public AdminUserSeeder(UsuarioRepository usuarioRepository,
                            PasswordEncoder passwordEncoder,
                            @Value("${app.admin.username}") String adminUsername,
                            @Value("${app.admin.initial-password}") String adminInitialPassword) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminInitialPassword = adminInitialPassword;
    }

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByUsername(adminUsername).isPresent()) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setUsername(adminUsername);
        admin.setPassword(passwordEncoder.encode(adminInitialPassword));
        admin.setEnabled(true);
        admin.setRoles(Set.of(RolNombre.ADMIN));

        usuarioRepository.save(admin);
        log.info("Usuario admin inicial creado: {}", adminUsername);
    }
}
