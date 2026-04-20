package co.edu.login.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarUsuarioDTO {
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
    private int telefono;
}
