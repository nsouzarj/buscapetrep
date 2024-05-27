package eti.buscapet.buscapet.controller

import eti.buscapet.buscapet.domain.CadastroPet
import eti.buscapet.buscapet.dtos.CadastroPetDTO
import eti.buscapet.buscapet.dtos.DtosConv
import eti.buscapet.buscapet.service.CadastroPetService
import eti.buscapet.buscapet.service.GoogleDriveService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.security.GeneralSecurityException

/**
 * Controlador REST para gerenciar operações relacionadas ao cadastro de pets.
 */
@RestController
@CrossOrigin(origins = arrayOf("*"))
@RequestMapping("/pet")
class CadastroPetController {

    @Autowired
    private lateinit var service: CadastroPetService

    @Autowired
    private lateinit var googleDriveService: GoogleDriveService;
    // Injetando a classe de conversão de DTOs
    val dtosConv: DtosConv = DtosConv();
    val folderId= "1w667RDrWL-zIUdNg3Bi7mpYcccOps7B3";


    /**
     * Cria um novo cadastro de pet.
     *
     * @param cadastroPetDTO O DTO contendo os dados do cadastro do pet.
     * @return O DTO do novo cadastro de pet criado.
     */
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

    /**
     * Busca um cadastro de pet por ID.
     *
     * @param id O ID do cadastro de pet a ser buscado.
     * @return O DTO do cadastro de pet encontrado, ou um ResponseEntity com status 404 se não encontrado.
     */
    @GetMapping("/{id}")
    fun buscarCadastroPetPorId(@PathVariable id: Long): ResponseEntity<CadastroPetDTO> {
        val cadastroPet = service.buscarPorId(id)
        val cadastroPetDTO = cadastroPet?.let {
            dtosConv.toCadastroPetDTO(it)
        }
        return if (cadastroPetDTO != null) ResponseEntity.ok(cadastroPetDTO) else ResponseEntity.notFound().build()
    }

    /**
     * Lista todos os cadastros de pets.
     *
     * @return Uma lista de DTOs de cadastros de pets.
     */
    @GetMapping("/listapet")
    @CrossOrigin(origins = arrayOf("*"))
    fun listarTodosCadastroPets(): ResponseEntity<List<CadastroPetDTO?>> {
        val todosCadastroPets = service.listarTodos()
        val todosCadastroPetsDTO = todosCadastroPets?.map {
            it?.let { it1 -> dtosConv.toCadastroPetDTO(it1) } // Mapear cada entidade para DTO
        }
        return ResponseEntity.ok(todosCadastroPetsDTO)
    }

    /**
     * Atualiza um cadastro de pet existente.
     *
     * @param id O ID do cadastro de pet a ser atualizado.
     * @param cadastroPet O novo cadastro de pet a ser usado para atualizar.
     * @return O cadastro de pet atualizado, ou um ResponseEntity com status 404 se não encontrado.
     */
    @PutMapping("/{id}")
    fun atualizarCadastroPet(@PathVariable id: Long, @RequestBody cadastroPet: CadastroPet): ResponseEntity<CadastroPet> {
        if (service.buscarPorId(id) != null) {
            cadastroPet.id = id
            val cadastroPetAtualizado = service.atualizar(cadastroPet)
            return ResponseEntity.ok(cadastroPetAtualizado)
        }
        return ResponseEntity.notFound().build()
    }

    /**
     * Deleta um cadastro de pet por ID.
     *
     * @param id O ID do cadastro de pet a ser deletado.
     * @return Um ResponseEntity com status 200 se a deleção for bem-sucedida, ou um ResponseEntity com status 404 se não encontrado.
     */
    @DeleteMapping("/{id}")
    fun deletarCadastroPet(@PathVariable id: Long): ResponseEntity<Void> {
        if (service.buscarPorId(id) != null) {
            service.deletar(id)
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.notFound().build()
    }

    /**
     * @param files
     * @return
     */

    @PostMapping("/uploadimage")
    @Throws(IOException::class)
    fun uploadFiles(@RequestParam("files") files: List<MultipartFile>): String {
        val response = StringBuilder()
        //Autetica no google dirve
        googleDriveService.getDriveService();
        for (file in files) {
            val uploadResult = googleDriveService.uploadFile(file)
            response.append(uploadResult).append("<br>")
        }
        return response.toString()
    }

    /**
     * Retorna uma imagem de um pet pelo nome do arquivo.
     *
     * @param nome O nome do arquivo da imagem a ser recuperada.
     * @return A imagem como uma ResponseEntity, ou um ResponseEntity com status 404 se a imagem não for encontrada.
     * @throws IOException Se ocorrer algum erro durante a leitura da imagem.
     */
    @GetMapping("/imagem/{nome}")
    @CrossOrigin(origins = arrayOf("*"))
    @Throws(IOException::class, GeneralSecurityException::class)
    fun getImage(@PathVariable nome: String): ResponseEntity<Any?> {
        // Obtenha o ID do arquivo na pasta "imagespet" no Google Drive
        googleDriveService.getDriveService()
        val fileId = nome;
        if (fileId == null) {
            return ResponseEntity.notFound().build()
        }

       // googleDriveService.getDriveService();
        val imageBytes = googleDriveService.downloadFileByName(nome,folderId, retries = 4);
        if (imageBytes == null) {
            return ResponseEntity.notFound().build()
        }

        val compressedImageBytes = imageBytes.toByteArray();

        // Defina o tipo de conteúdo
        val headers = HttpHeaders()
        headers.setContentType(MediaType.IMAGE_JPEG) // Ajuste o tipo de conteúdo de acordo com a imagem

        // Retorne a imagem como uma ResponseEntity
        return ResponseEntity<Any?>(compressedImageBytes, headers, HttpStatus.OK)
    }



}