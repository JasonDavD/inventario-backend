package com.inventario.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                           JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                           JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher(new AntPathRequestMatcher("/api/**"))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/productos/**", "/api/categorias/**", "/api/proveedores/**")
                    .hasAnyRole("ADMIN", "OPERADOR", "LECTOR")
                .requestMatchers(HttpMethod.POST, "/api/productos/**").hasAnyRole("ADMIN", "OPERADOR")
                .requestMatchers(HttpMethod.PUT, "/api/productos/**").hasAnyRole("ADMIN", "OPERADOR")
                // Regla especifica antes que la general: borrar UNA imagen es una accion
                // menor (mismo nivel que crear/editar), borrar el producto entero es solo ADMIN.
                .requestMatchers(HttpMethod.DELETE, "/api/productos/*/imagenes/**").hasAnyRole("ADMIN", "OPERADOR")
                .requestMatchers(HttpMethod.DELETE, "/api/productos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/categorias/**", "/api/proveedores/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/categorias/**", "/api/proveedores/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/categorias/**", "/api/proveedores/**").hasRole("ADMIN")
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
            .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/h2-console/**", "/error").permitAll()
                // Rutas de mutacion primero: si no, el catch-all de GET de mas abajo
                // les daria acceso de lectura a cualquier rol antes de llegar a esta regla.
                .requestMatchers("/productos/eliminar/**", "/categorias/eliminar/**", "/proveedores/eliminar/**")
                    .hasRole("ADMIN")
                .requestMatchers("/categorias/nuevo", "/categorias/guardar", "/categorias/editar/**", "/categorias/actualizar")
                    .hasRole("ADMIN")
                .requestMatchers("/proveedores/nuevo", "/proveedores/guardar", "/proveedores/editar/**", "/proveedores/actualizar")
                    .hasRole("ADMIN")
                .requestMatchers("/productos/nuevo", "/productos/guardar", "/productos/editar/**", "/productos/actualizar")
                    .hasAnyRole("ADMIN", "OPERADOR")
                .requestMatchers(HttpMethod.GET, "/productos/**", "/categorias/**", "/proveedores/**")
                    .hasAnyRole("ADMIN", "OPERADOR", "LECTOR")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/productos")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}
