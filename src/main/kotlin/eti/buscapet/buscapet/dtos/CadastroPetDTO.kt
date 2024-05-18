package eti.buscapet.buscapet.dtos

import java.util.*

data class CadastroPetDTO (
    val id: Long? = null,
    val nomeAnimal: String? = null,
    val tipo: String? = null,
    val raca: String? = null,
    val idade: String? = null,
    val chipado: Boolean = false,
    val vacinado: Boolean = false,
    val castrado: Boolean = false,
    val descricao: String? = null,
    val situacao: String? = null,
    val datadodesaparecimento: Date? = null,
    var endereco: String? = null,
    val bairro: String? = null,
    var cidade: String? = null,
    var estado:String? = null,
    val nomeTutor: String? = null,
    val email: String? = null,
    val celular: String? = null,
    val imagens: List<ImagemPetDTO>? = null
)