package com.inventario.controller;

import com.inventario.dto.UsuarioRequest;
import com.inventario.dto.UsuarioResponse;
import com.inventario.model.Usuario;
import com.inventario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioRestController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping
    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponse::from)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@RequestBody UsuarioRequest req) {
        Usuario usuario = new Usuario();
        usuario.setUsername(req.getUsername());
        usuario.setPassword(passwordEncoder.encode(req.getPassword()));
        usuario.setEnabled(req.getEnabled() == null || req.getEnabled());
        usuario.setRoles(req.getRoles());

        Usuario guardado = usuarioRepository.save(usuario);
        return ResponseEntity.ok(UsuarioResponse.from(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id, @RequestBody UsuarioRequest req) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    if (req.getUsername() != null) {
                        usuario.setUsername(req.getUsername());
                    }
                    if (req.getPassword() != null && !req.getPassword().isBlank()) {
                        usuario.setPassword(passwordEncoder.encode(req.getPassword()));
                    }
                    if (req.getEnabled() != null) {
                        usuario.setEnabled(req.getEnabled());
                    }
                    if (req.getRoles() != null && !req.getRoles().isEmpty()) {
                        usuario.setRoles(req.getRoles());
                    }
                    return ResponseEntity.ok(UsuarioResponse.from(usuarioRepository.save(usuario)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        return usuarioRepository.findById(id)
                .map(usuario -> {
                    usuarioRepository.delete(usuario);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
