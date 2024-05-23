package eti.buscapet.buscapet.controller

import eti.buscapet.buscapet.domain.Usuario
import eti.buscapet.buscapet.dtos.DtosConv
import eti.buscapet.buscapet.dtos.UsuarioDTO
import eti.buscapet.buscapet.service.EmailService
import eti.buscapet.buscapet.service.GoogleDriveService
import eti.buscapet.buscapet.service.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.MessageDigest

/**
 * Controlador REST para gerenciar usuários.
 */
@RestController
@CrossOrigin(origins = arrayOf("*"))
@RequestMapping("/usuarios")
class UsuarioController {

    @Autowired private lateinit var usuarioService: UsuarioService

    // Serviço de email
    @Autowired lateinit var emailService: EmailService
    @Autowired lateinit var serviceGoogle: GoogleDriveService;

    val dtosConv: DtosConv = DtosConv();

    /**
     * Cria um novo usuário.
     *
     * @param usuarioDTO Objeto UsuarioDTO com os dados do novo usuário.
     * @return ResponseEntity com o usuário criado ou um erro caso o email já exista.
     */
    @PostMapping("/caduser")
    fun criarUsuario(@RequestBody usuarioDTO: UsuarioDTO): ResponseEntity<Usuario> {
        // Verifique se o email já existe na base de dados
        val usuarioExistente = usuarioDTO.emailUser?.let { usuarioService.buscaUsuarioPorEmail(it) }

        // Se o email já existir, retorne um erro (409 CONFLICT)
        if (usuarioExistente != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null)
        }

        // Se o email não existir, crie o usuário e retorne um status 201 (CREATED)
        val novoUsuario = usuarioService.criarUsuario(usuarioDTO)
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario)
    }

    /**
     * Obtem um usuário pelo ID.
     *
     * @param id ID do usuário.
     * @return ResponseEntity com o usuário encontrado ou um erro caso o usuário não seja encontrado.
     */
    @GetMapping("/{id}")
    fun obterUsuario(@PathVariable id: Long): ResponseEntity<UsuarioDTO> {
        val usuario = usuarioService.obterUsuario(id)
        val userDto = usuario?.let { dtosConv.toUsuarioDTO(it) };
        return if (userDto != null) {
            ResponseEntity.ok(userDto)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Lista todos os usuários.
     *
     * @return ResponseEntity com a lista de usuários.
     */
    @GetMapping("/lista")
    fun listarUsuarios(): ResponseEntity<List<UsuarioDTO?>> {
        val usuarios = usuarioService.listarUsuarios();
        val usuarioDTO = usuarios?.map {
            it?.let { it1 -> dtosConv.toUsuarioDTO(it1) } // Mapear cada entidade para DTO
        }
        return ResponseEntity.ok(usuarioDTO)
    }

    /**
     * Atualiza um usuário.
     *
     * @param id ID do usuário a ser atualizado.
     * @param usuarioDTO Objeto UsuarioDTO com os novos dados do usuário.
     * @return ResponseEntity com o usuário atualizado ou um erro caso o usuário não seja encontrado.
     */
    @PutMapping("/{id}")
    fun atualizarUsuario(@PathVariable id: Long, @RequestBody usuarioDTO: UsuarioDTO): ResponseEntity<Usuario> {
        val usuarioAtualizado = usuarioService.atualizarUsuario(id, usuarioDTO)
        return if (usuarioAtualizado != null) {
            ResponseEntity.ok(usuarioAtualizado)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Exclui um usuário.
     *
     * @param id ID do usuário a ser excluído.
     * @return ResponseEntity com status 204 (NO CONTENT) se o usuário foi excluído com sucesso ou 404 (NOT FOUND) caso o usuário não seja encontrado.
     */
    @DeleteMapping("/{id}")
    fun deleteUsuer(@PathVariable id: Long): ResponseEntity<Usuario> {
        val deletado = usuarioService.deletarUsuario(id)
        return if (deletado) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    /**
     * Verifica se o usuário está cadastrado.
     *
     * @param email Email do usuário.
     * @param senhaHash Senha do usuário criptografada.
     * @return Booleano indicando se o usuário está cadastrado.
     */
    @GetMapping("/login/{email}/{senhaHash}")
    fun loginUsuer(@PathVariable email: String, @PathVariable senhaHash: String): Boolean {
        val usuario = usuarioService.buscaUsuarioPorEmail(email)
        if (usuario?.emailUser?.isNotEmpty() == true) {
            if (usuario.senhaUser == senhaHash) {
                return true
            } else {
                return false
            }
        } else {
            return false
        }
    }

    /**
     * Gera um código de recuperação de senha para o usuário.
     *
     * @param email Email do usuário.
     * @return Booleano indicando se o código foi gerado e enviado com sucesso.
     */
    @GetMapping("/gerarcodigo/{email}")
    fun gerarCodigo(@PathVariable email: String): Boolean {
        var teste: Boolean = false;
        val usuario = usuarioService.buscaUsuarioPorEmail(email)

        if (usuario?.emailUser?.isNotEmpty() == true) {
            var codigo = usuarioService.gerarCodigo();
            usuario.codrecover = codigo;
            val userDto = usuario?.let { dtosConv.toUsuarioDTO(it) };
            if (userDto != null) {
                usuario.id?.let { usuarioService.atualizarUsuario(it, userDto) }
                emailService.sendSimpleMessage(userDto.emailUser, "Código de Seguraça", "Código de recuperação de senha:  " + userDto.codrecover);
                teste = true;
            } else {
                teste = false;
            }
        }
        return teste;
    }

    /**
     * Altera a senha do usuário.
     *
     * @param email Email do usuário.
     * @param senha Nova senha do usuário.
     * @param codigogerado Código de recuperação de senha gerado anteriormente.
     * @return Booleano indicando se a senha foi alterada com sucesso.
     */
    @GetMapping("/alterarsenha/{email}/{senha}/{codigogerado}")
    fun alterarSenha(@PathVariable email: String, @PathVariable senha: String, @PathVariable codigogerado: String): Boolean {
        var teste: Boolean = false;
        val usuario = usuarioService.buscaUsuarioPorEmail(email)

        if (usuario?.emailUser?.isNotEmpty() == true && usuario?.codrecover == codigogerado) {
            usuario.codrecover = null;
            usuario.senhaUser = senha;
            val userDto = usuario?.let { dtosConv.toUsuarioDTO(it) };
            if (userDto != null) {
                usuario.id?.let { usuarioService.atualizarUsuario(it, userDto) }
                emailService.sendSimpleMessage(userDto.emailUser, "Senha alterada.", "Caro usuário "
                        + userDto.nomeUser + ", sua senha foi alterada com sucesso!");
                teste = true;

            } else {
                teste = false;
            }
        }
        return teste;
    }


    /**
     * Gera o hash SHA-256 da senha.
     *
     * @param senha Senha a ser criptografada.
     * @return String com o hash SHA-256 da senha.
     */
    private fun gerarHashSHA256(senha: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(senha.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }
}