package co.edu.login.controller;

import co.edu.login.DTO.LoginDTO;
import co.edu.login.DTO.PerfilUsuarioDTO;
import co.edu.login.DTO.RegistrarUsuarioDTO;
import co.edu.login.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;

    @PostMapping("/registrar")
    public ResponseEntity<TokenResponse> registrarUsuario(@RequestBody final RegistrarUsuarioDTO request) {
        final TokenResponse token = service.registrarUsuario(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody final LoginDTO request) {
        final TokenResponse token = service.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refrescar")
    public TokenResponse refrescarToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        return service.refrescarToken(authHeader);
    }

    @GetMapping("/perfil")
    public ResponseEntity<PerfilUsuarioDTO> perfil(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(service.obtenerPerfil(userDetails.getUsername()));
    }
}
