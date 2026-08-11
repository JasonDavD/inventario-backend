package com.inventario.dto;

import com.inventario.model.RolNombre;
import com.inventario.model.Usuario;

import java.time.LocalDateTime;
import java.util.Set;

public class UsuarioResponse {

    private Long id;
    private String username;
    private boolean enabled;
    private Set<RolNombre> roles;
    private LocalDateTime fechaCreacion;

    public static UsuarioResponse from(Usuario usuario) {
        UsuarioResponse dto = new UsuarioResponse();
        dto.id = usuario.getId();
        dto.username = usuario.getUsername();
        dto.enabled = usuario.isEnabled();
        dto.roles = usuario.getRoles();
        dto.fechaCreacion = usuario.getFechaCreacion();
        return dto;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public boolean isEnabled() { return enabled; }
    public Set<RolNombre> getRoles() { return roles; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
