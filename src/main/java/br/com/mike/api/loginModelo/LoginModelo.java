package br.com.mike.api.loginModelo;


import br.com.mike.record.UsuarioRecord;
import org.jetbrains.annotations.NotNull;

public class LoginModelo {

    private UsuarioRecord usuario;
    private Boolean manterLogado = false;

    public LoginModelo() {
    }

    public @NotNull UsuarioRecord getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioRecord usuario) {
        this.usuario = usuario;
    }

    public Boolean getManterLogado() {
        return manterLogado;
    }

    public void setManterLogado(Boolean manterLogado) {
        this.manterLogado = manterLogado;
    }
}
