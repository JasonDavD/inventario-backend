package com.inventario.dto;

import com.inventario.model.RolNombre;

import java.util.Set;

public class UsuarioRequest {

    private String username;
    private String password;
    private Boolean enabled;
    private Set<RolNombre> roles;

    public UsuarioRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Set<RolNombre> getRoles() { return roles; }
    public void setRoles(Set<RolNombre> roles) { this.roles = roles; }
}
