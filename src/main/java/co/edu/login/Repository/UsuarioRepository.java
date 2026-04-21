package co.edu.login.Repository;

import co.edu.login.Model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
}




