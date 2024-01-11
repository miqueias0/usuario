package br.com.mike.modelo;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {
    @Id
    @Column(name = "id", nullable = false, unique = true, updatable = false)
    private String id;
    @Column(nullable = false)
    private String nomeUsuario;
    @Column(nullable = false)
    private String email;
    private String telefone;
    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private String tipoUsuario;

    public Usuario() {
    }

    public Usuario(String id, String nomeUsuario, String email, String telefone, String senha, String tipoUsuario) {
        this.id = id;
        this.nomeUsuario = nomeUsuario;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
        this.tipoUsuario = tipoUsuario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return id.equals(usuario.id) && Objects.equals(nomeUsuario, usuario.nomeUsuario)
                && Objects.equals(email, usuario.email) && Objects.equals(telefone, usuario.telefone)
                && Objects.equals(tipoUsuario, usuario.tipoUsuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nomeUsuario, email, telefone, tipoUsuario);
    }


}
