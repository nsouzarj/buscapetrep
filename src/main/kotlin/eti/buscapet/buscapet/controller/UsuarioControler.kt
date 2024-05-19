package eti.buscapet.buscapet.controller
import eti.buscapet.buscapet.domain.Usuario
import eti.buscapet.buscapet.dtos.DtosConv
import eti.buscapet.buscapet.dtos.UsuarioDTO
import eti.buscapet.buscapet.service.EmailService
import eti.buscapet.buscapet.service.UsuarioService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.MessageDigest

@RestController
@CrossOrigin(origins = arrayOf("*"))
@RequestMapping("/usuarios")
class UsuarioController {

    @Autowired private lateinit var usuarioService: UsuarioService

    //Servico de email
    @Autowired lateinit var emailService:EmailService

    val dtosConv: DtosConv = DtosConv();
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

    @GetMapping("/lista")
    fun listarUsuarios(): ResponseEntity<List<UsuarioDTO?>> {
        val usuarios = usuarioService.listarUsuarios();
        val usuarioDTO = usuarios?.map {
            it?.let { it1 -> dtosConv.toUsuarioDTO(it1) } // Mapear cada entidade para DTO
        }
        return ResponseEntity.ok(usuarioDTO)
    }

    @PutMapping("/{id}")
    fun atualizarUsuario(@PathVariable id: Long, @RequestBody usuarioDTO: UsuarioDTO): ResponseEntity<Usuario> {
        val usuarioAtualizado = usuarioService.atualizarUsuario(id, usuarioDTO)
        return if (usuarioAtualizado != null) {
            ResponseEntity.ok(usuarioAtualizado)
        } else {
            ResponseEntity.notFound().build()
        }
    }

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
     * Metodo para verifica se o suaurio esta cadastrado
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
     * Metodo que gera o codigo para o usuario
     */
    @GetMapping("/gerarcodigo/{email}")
    fun gerarCodigo(@PathVariable email: String):Boolean {
        var teste:Boolean=false;
         val usuario = usuarioService.buscaUsuarioPorEmail(email)

        if (usuario?.emailUser?.isNotEmpty() == true) {
            var codigo = usuarioService.gerarCodigo();
            usuario.codrecover = codigo;
            val userDto = usuario?.let { dtosConv.toUsuarioDTO(it) };
            if (userDto != null) {
                usuario.id?.let { usuarioService.atualizarUsuario(it, userDto) }
                emailService.sendSimpleMessage(userDto.emailUser,"Código de Seguraça","Código de recuperação de senha:  "+userDto.codrecover);
                teste=true;
            } else {
                teste = false;
            }
        }
        return teste;
    }

    @GetMapping("/alterarsenha/{email}/{senha}/{codigogerado}")
    fun alterarSenha(@PathVariable email: String, @PathVariable senha:String,@PathVariable codigogerado:String):Boolean {
        var teste:Boolean=false;
        val usuario = usuarioService.buscaUsuarioPorEmail(email)

        if (usuario?.emailUser?.isNotEmpty() == true && usuario?.codrecover==codigogerado) {
            usuario.codrecover = null;
            usuario.senhaUser=senha;
            val userDto = usuario?.let { dtosConv.toUsuarioDTO(it) };
            if (userDto != null) {
                usuario.id?.let { usuarioService.atualizarUsuario(it, userDto) }
                emailService.sendSimpleMessage(userDto.emailUser,"Senha alterada.","Caro usuário "
                        +userDto.nomeUser+", sua senha foi alterada com sucesso!" );
                teste=true;

            } else {
                teste = false;
            }
        }
        return teste;
    }

    private fun gerarHashSHA256(senha: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(senha.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

}