package br.com.mike.usuario.repository;

import br.com.mike.usuario.dao.IUsuario;
import br.com.mike.usuario.modelo.Usuario;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioService {
    @Autowired(required = true)
    private IUsuario iUsuario;

    public Usuario manter(@NotNull Usuario usuario) throws Exception{
        usuario.setId(UUID.randomUUID().toString());
        usuario.setEmail(usuario.getEmail().toLowerCase());
        if(iUsuario.findByEmail(usuario.getEmail().toLowerCase()) != null){
            throw new Exception("Email já cadastrado");
        }
        iUsuario.save(usuario);
        return usuario;
    }

    public Usuario update(@NotNull Usuario usuario) throws Exception {
        Usuario entity = iUsuario.findById(usuario.getId()).orElse(null);
        if(entity != null){
            entity.setEmail(usuario.getEmail());
            entity.setNomeUsuario(usuario.getNomeUsuario());
            entity.setTelefone(usuario.getTelefone());
            if(usuario.getSenha().equals(entity.getSenha())){
                throw new Exception("Senha não pode ser igual a anterior");
            }
            entity.setSenha(usuario.getSenha());
            iUsuario.save(entity);
            return entity;
        }
        return null;
    }

    public Usuario obterPorId(String id){
        return iUsuario.findById(id).orElse(null);
    }

    public Usuario obterPorNomeUsuario(String nomeUsuario){
        return iUsuario.findByNomeUsuario(nomeUsuario);
    }

    public Usuario obterPorEmail(String email){
        return iUsuario.findByEmail(email);
    }

    public Usuario obterPorTelefone(String telefone){
        return iUsuario.findByTelefone(telefone);
    }

    public Usuario login(@NotNull Usuario usuario) throws Exception{
        Usuario logado;
        logado = iUsuario.findByEmailAndSenha(usuario.getEmail(), usuario.getSenha());
        if(logado == null){
            throw new Exception("Usuário não encontrado!");
        }
        return logado;
    }

}
