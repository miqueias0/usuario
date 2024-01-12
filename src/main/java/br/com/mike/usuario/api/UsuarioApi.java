package br.com.mike.usuario.api;

import br.com.mike.usuario.modelo.Usuario;
import br.com.mike.usuario.record.LoginRecord;
import br.com.mike.usuario.record.UsuarioRecord;
import br.com.mike.comum.records.AutenticacaoRecord;
import br.com.mike.comum.records.TokenRecord;
import br.com.mike.usuario.repository.UsuarioService;
import br.com.mike.comum.service.AutenticationService;
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
    private AutenticationService seguranca =  new AutenticationService("http://localhost:8081");;

    public UsuarioApi() {
    }

    @GetMapping(value = "/obterPorId")
    public ResponseEntity<UsuarioRecord> obterPorId(@Context HttpHeaders sec) throws Exception {
        try {
            AutenticacaoRecord autenticacaoRecord = seguranca.validarToken(sec.get("authorization").get(0));
            UsuarioRecord usuarioRecord = converterUsuario(usuarioService.obterPorId(autenticacaoRecord.identificador()));
            return ResponseEntity.ok().body(usuarioRecord);
        } catch (Exception e) {
            throw new Exception("Erro ao obter por id pelo seguinte motivo: " + (e.getLocalizedMessage() != null ? e.getLocalizedMessage(): ""));
        }

    }

    @PostMapping(value = "/manter")
    public ResponseEntity<TokenRecord> manter(@RequestBody UsuarioRecord usuarioRecord) throws Exception {
        try {
            userTransaction.begin();
            TokenRecord token = seguranca.criarToken(usuarioService.manter(converterUsuarioRecord(usuarioRecord)).getId(), 300);
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
    public ResponseEntity<TokenRecord> login(@RequestBody LoginRecord loginRecord) throws Exception{
        try {
            Usuario usuario = usuarioService.login(converterUsuarioRecord(loginRecord.usuario()));
            TokenRecord tokenRecord = seguranca.criarToken(
                    usuario.getId(), loginRecord.manterLogado() == null || loginRecord.manterLogado() ? null: 300);
            return ResponseEntity.ok().body(tokenRecord);
        } catch (Exception e) {
            throw new Exception((e.getLocalizedMessage() != null ? e.getLocalizedMessage(): ""));
        }
    }

    @GetMapping(value = "/loginComToken")
    public ResponseEntity<TokenRecord> loginComToken(@Context HttpHeaders sec) {
        try {
            TokenRecord tokenRecord = seguranca.atualizarToken(sec.get("authorization").get(0));
            return ResponseEntity.ok().body(tokenRecord);
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
