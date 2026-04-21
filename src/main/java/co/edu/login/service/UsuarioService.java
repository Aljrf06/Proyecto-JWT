package co.edu.login.service;

import co.edu.login.DTO.LoginDTO;
import co.edu.login.DTO.PerfilUsuarioDTO;
import co.edu.login.DTO.RegistrarUsuarioDTO;
import co.edu.login.Model.Usuario;
import co.edu.login.Repository.UsuarioRepository;
import co.edu.login.controller.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repositorio;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authManager;

    public TokenResponse registrarUsuario(RegistrarUsuarioDTO dto) {
        var usuario = Usuario.builder()
                .nombre(dto.getNombre())
                .apellido(dto.getApellido())
                .correo(dto.getCorreo())
                .contrasena(passwordEncoder.encode(dto.getContrasena()))
                .telefono(Long.parseLong(dto.getTelefono()))
                .build();
        repositorio.save(usuario);
        return new TokenResponse(
                jwtService.generarToken(usuario),
                jwtService.generarRefreshToken(usuario)
        );
    }

    public TokenResponse login(LoginDTO dto) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getCorreo(), dto.getContrasena())
        );
        var usuario = repositorio.findByCorreo(dto.getCorreo()).orElseThrow();
        return new TokenResponse(
                jwtService.generarToken(usuario),
                jwtService.generarRefreshToken(usuario)
        );
    }

    public TokenResponse refrescarToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token invalido");
        }
        String refreshToken = authHeader.substring(7);
        String correo = jwtService.extraerUsuario(refreshToken);
        var usuario = repositorio.findByCorreo(correo).orElseThrow();
        if (!jwtService.esTokenValido(refreshToken, usuario)) {
            throw new RuntimeException("Token expirado");
        }
        return new TokenResponse(jwtService.generarToken(usuario), refreshToken);
    }

    public PerfilUsuarioDTO obtenerPerfil(String correo) {
        var usuario = repositorio.findByCorreo(correo).orElseThrow();
        return new PerfilUsuarioDTO(
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getCorreo(),
                usuario.getTelefono()
        );
    }
}
