package co.edu.login.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PerfilUsuarioDTO {
    private String nombre;
    private String apellido;
    private String correo;
    private long telefono;
}
