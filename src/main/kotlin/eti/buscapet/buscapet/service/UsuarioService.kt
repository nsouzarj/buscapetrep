package eti.buscapet.buscapet.service
import eti.buscapet.buscapet.domain.Usuario
import eti.buscapet.buscapet.dtos.DtosConv
import eti.buscapet.buscapet.dtos.UsuarioDTO
import eti.buscapet.buscapet.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.random.Random


@Service
class UsuarioService {

    @Autowired
    private lateinit var usuarioRepository: UsuarioRepository
    val dtos:DtosConv = DtosConv();
    fun criarUsuario(usuarioDTO: UsuarioDTO): Usuario {

        val usuario=dtos.toUsuario(usuarioDTO);
        return usuarioRepository.save(usuario)
    }

    fun obterUsuario(id: Long): Usuario? {
        return usuarioRepository.findById(id).orElse(null)
    }

    fun listarUsuarios(): List<Usuario> {
        return usuarioRepository.findAll()
    }

    fun atualizarUsuario(id: Long, usuarioDTO: UsuarioDTO): Usuario? {
        val usuarioExistente = usuarioRepository.findById(id).orElse(null)
        if (usuarioExistente != null) {
            usuarioExistente.nomeUser = usuarioDTO.nomeUser
            usuarioExistente.emailUser = usuarioDTO.emailUser
            usuarioExistente.celularUser = usuarioDTO.celularUser
            usuarioExistente.senhaUser = usuarioDTO.senhaUser
            usuarioExistente.codrecover= usuarioDTO.codrecover// Idealmente, faça hashing da senha aqui
            return usuarioRepository.save(usuarioExistente)
        }
        return null
    }

    fun deletarUsuario(id: Long): Boolean {
        return if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    fun buscaUsuarioPorEmail(email:String):Usuario?{
        return usuarioRepository.findUserByEmail(email);

    }

    //Gera o codigo do uuario
    fun gerarCodigo():String{
        val randomNumber = Random.nextInt(100000, 1000000) // Gera um número aleatório entre 100000 e 999999
        return randomNumber.toString();
    }
}