package eti.buscapet.buscapet.dtos

data class ImagemPetDTO(
    var id: Long? = null,
    var caminhoImagem: String? =null,
    var nomeArquivo: String?=null,
    var tipo: String?=null
)