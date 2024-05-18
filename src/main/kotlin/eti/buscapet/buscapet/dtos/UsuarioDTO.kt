package eti.buscapet.buscapet.dtos

import jakarta.persistence.Column

class UsuarioDTO (
    val id: Long? = null,
    val nomeUser: String?=null,
    var endereco:String?= null,
    var bairro:String?= null,
    var cep:String?= null,
    var cidade:String?= null,
    var estado:String?= null,
    val emailUser: String?=null,
    val celularUser: String?=null,
    val senhaUser: String?=null,
    var codrecover: String? =null,

)