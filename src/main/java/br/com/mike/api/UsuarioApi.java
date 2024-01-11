package br.com.mike.api;

import br.com.mike.api.loginModelo.LoginModelo;
import br.com.mike.modelo.Usuario;
import br.com.mike.record.TokenRecord;
import br.com.mike.record.UsuarioRecord;
import br.com.mike.repository.UsuarioService;
import br.com.mike.seguranca.controle.Seguranca;
import br.com.mike.seguranca.modelo.Autenticacao;
import jakarta.inject.Inject;
import jakarta.transaction.UserTransaction;
import jakarta.ws.rs.core.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/usuario")
public class UsuarioApi {

    @Autowired
    private UsuarioService usuarioService;
    @Inject
    private UserTransaction userTransaction;
    private final Seguranca seguranca = new Seguranca();

    public UsuarioApi() {
    }

    @GetMapping(value = "/obterPorId")
    public ResponseEntity<UsuarioRecord> obterPorId(@Context HttpHeaders sec) throws Exception {
        Autenticacao autenticacao;
        try {
            autenticacao = seguranca.validarToken(sec.get("authorization").get(0));
            UsuarioRecord usuarioRecord = converterUsuario(usuarioService.obterPorId(autenticacao.getIdentificador()));
            return ResponseEntity.ok().body(usuarioRecord);
        } catch (Exception e) {
            throw new Exception("Erro ao obter por id pelo seguinte motivo: " + (e.getLocalizedMessage() != null ? e.getLocalizedMessage(): ""));
        }

    }

    @PostMapping(value = "/manter")
    public ResponseEntity<TokenRecord> manter(@RequestBody UsuarioRecord usuarioRecord) throws Exception {
        try {
            userTransaction.begin();
            TokenRecord token = new TokenRecord(seguranca.criarToken(usuarioService.manter(converterUsuarioRecord(usuarioRecord)).getId(), 300));
            userTransaction.commit();
            return new ResponseEntity<>(token, HttpStatus.CREATED);
        } catch (Exception e) {
            userTransaction.rollback();
            throw new Exception("Erro ao manter usuario pelo seguinte motivo: " + (e.getLocalizedMessage() != null ? e.getLocalizedMessage(): ""));
        }
    }

    @PostMapping(value = "/alterarUsuario")
    public ResponseEntity<UsuarioRecord> alterarUsuario(@RequestBody UsuarioRecord usuarioRecord, @Context HttpHeaders sec) throws Exception {
        try {
            seguranca.validarToken(sec.get("authorization").get(0));
            userTransaction.begin();
            UsuarioRecord usuarioAtualizado = converterUsuario(usuarioService.update(converterUsuarioRecord(usuarioRecord)));
            userTransaction.commit();
            return ResponseEntity.ok().body(usuarioAtualizado);
        } catch (Exception e) {
            userTransaction.rollback();
            throw new RuntimeException("Erro ao alterar usuario pelo seguinte motivo: " + (e.getLocalizedMessage() != null ? e.getLocalizedMessage(): ""));
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<TokenRecord> login(@RequestBody LoginModelo loginModelo) throws Exception{
        try {
            Usuario usuario = usuarioService.login(converterUsuarioRecord(loginModelo.getUsuario()));
            return ResponseEntity.ok().body(new TokenRecord(seguranca.criarToken(
                    usuario.getId(), loginModelo.getManterLogado()? null: 300)));
        } catch (Exception e) {
            throw new Exception((e.getLocalizedMessage() != null ? e.getLocalizedMessage(): ""));
        }
    }

    @GetMapping(value = "/loginComToken")
    public ResponseEntity<TokenRecord> loginComToken(@Context HttpHeaders sec) {
        try {
            return ResponseEntity.ok().body(new TokenRecord(seguranca.criarToken(
                    seguranca.validarToken(sec.get("authorization").get(0)).getIdentificador(), null)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Usuario converterUsuarioRecord(UsuarioRecord usuarioRecord){
        Usuario usuario = new Usuario();
        usuario.setNomeUsuario(usuarioRecord.nomeUsuario());
        usuario.setTipoUsuario(usuarioRecord.tipoUsuario());
        usuario.setEmail(usuarioRecord.email());
        usuario.setTelefone(usuarioRecord.telefone());
        usuario.setSenha(usuarioRecord.senha());
        return usuario;
    }

    private UsuarioRecord converterUsuario(Usuario usuario){
        UsuarioRecord usuarioRecord = new UsuarioRecord(usuario.getNomeUsuario(), usuario.getEmail(), usuario.getTelefone(), null, usuario.getTipoUsuario());
        return usuarioRecord;
    }

}
