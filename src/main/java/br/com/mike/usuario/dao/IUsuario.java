package br.com.mike.usuario.dao;

import br.com.mike.usuario.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface IUsuario extends JpaRepository<Usuario, String> {
    Optional<Usuario> findById(String id);
    Usuario findByNomeUsuario(String nomeUsuario);
    Usuario findByEmailAndSenha(String email, String senha);
    Usuario findByEmail(String email);
    Usuario findByTelefone(String telefone);

}
