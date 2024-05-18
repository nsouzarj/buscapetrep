package eti.buscapet.buscapet.controller

import eti.buscapet.buscapet.domain.CadastroPet
import eti.buscapet.buscapet.dtos.CadastroPetDTO
import eti.buscapet.buscapet.dtos.DtosConv
import eti.buscapet.buscapet.service.CadastroPetService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.file.Files
import javax.imageio.ImageIO


@RestController
@CrossOrigin(origins = arrayOf("*"))
@RequestMapping("/pet")
class CadastroPetController {

    @Autowired
    private lateinit var service: CadastroPetService
    val dtosConv:DtosConv = DtosConv();
    @CrossOrigin(origins = arrayOf("*"))
    @PostMapping("/cad")
    fun criarCadastroPet(@RequestBody cadastroPetDTO: CadastroPetDTO): ResponseEntity<CadastroPetDTO> {
        // Converte o DTO para a entidade

        val cadastroPet = dtosConv.toCadastroPet(cadastroPetDTO)

        // Salva a entidade usando o serviço
        val novoCadastroPet = service.salvar(cadastroPet)

        // Converte a entidade salva de volta para DTO
        val novoCadastroPetDTO = dtosConv.toCadastroPetDTO(novoCadastroPet)

        return ResponseEntity.ok(novoCadastroPetDTO)
    }


    @GetMapping("/{id}")
    fun buscarCadastroPetPorId(@PathVariable id: Long): ResponseEntity<CadastroPetDTO> {
        val cadastroPet = service.buscarPorId(id)
        val cadastroPetDTO= cadastroPet?.let {
            dtosConv.toCadastroPetDTO(it)
        }
        return if (cadastroPetDTO != null) ResponseEntity.ok(cadastroPetDTO) else ResponseEntity.notFound().build()
    }

    @GetMapping("/listapet")
    @CrossOrigin(origins = arrayOf("*"))
    fun listarTodosCadastroPets(): ResponseEntity<List<CadastroPetDTO?>> {
        val todosCadastroPets = service.listarTodos()
        val todosCadastroPetsDTO = todosCadastroPets?.map {
            it?.let { it1 -> dtosConv.toCadastroPetDTO(it1) } // Mapear cada entidade para DTO
        }
        return ResponseEntity.ok(todosCadastroPetsDTO)
    }

    @PutMapping("/{id}")
    fun atualizarCadastroPet(@PathVariable id: Long, @RequestBody cadastroPet: CadastroPet): ResponseEntity<CadastroPet> {
        if (service.buscarPorId(id) != null) {
            cadastroPet.id = id
            val cadastroPetAtualizado = service.atualizar(cadastroPet)
            return ResponseEntity.ok(cadastroPetAtualizado)
        }
        return ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun deletarCadastroPet(@PathVariable id: Long): ResponseEntity<Void> {
        if (service.buscarPorId(id) != null) {
            service.deletar(id)
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.notFound().build()
    }

    @PostMapping("/uploadimage")
    @Throws(IOException::class)
    fun uploadFiles(@RequestParam("files") files: List<MultipartFile>): String {
        // Obtenha o diretório de destino
        val uploadDir = File("/home/nelson/imagespet")
        uploadDir.mkdirs() // Cria o diretório se não existir

        // Itera sobre cada arquivo e salva
        for (file in files) {
            val destFile = File(uploadDir, file.originalFilename)
            file.transferTo(destFile)
        }
        return "Arquivos enviados com sucesso!"
    }



    @GetMapping("/imagem/{nome}")
    @CrossOrigin(origins = arrayOf("*"))
    @Throws(IOException::class)
    fun getImage(@PathVariable nome: String): ResponseEntity<Any?> {
        // Obtenha o caminho da imagem
        val imageFile = File("/home/nelson/imagespet/$nome")

        // Leia o conteúdo da imagem
        val imageBytes = Files.readAllBytes(imageFile.toPath())

        // **Compressão da imagem**
        val outputStream = ByteArrayOutputStream()
        ImageIO.write(ImageIO.read(ByteArrayInputStream(imageBytes)), "jpg", outputStream) // Supondo que a imagem seja JPEG
        val compressedImageBytes = outputStream.toByteArray()

        // Defina o tipo de conteúdo
        val headers = HttpHeaders()
        headers.setContentType(MediaType.IMAGE_JPEG) // Ajuste o tipo de conteúdo de acordo com a imagem

        // Retorne a imagem como uma ResponseEntity
        return ResponseEntity<Any?>(compressedImageBytes, headers, HttpStatus.OK)
    }

}
