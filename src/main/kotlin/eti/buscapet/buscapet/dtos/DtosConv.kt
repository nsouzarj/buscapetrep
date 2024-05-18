package eti.buscapet.buscapet.dtos

import eti.buscapet.buscapet.domain.CadastroPet
import eti.buscapet.buscapet.domain.ImagemPet
import eti.buscapet.buscapet.domain.Usuario

class DtosConv {
    fun toCadastroPet(cadastroPetDTO: CadastroPetDTO): CadastroPet {
        return CadastroPet(
            id = cadastroPetDTO.id,
            nomeAnimal = cadastroPetDTO.nomeAnimal,
            tipo = cadastroPetDTO.tipo,
            raca = cadastroPetDTO.raca,
            idade = cadastroPetDTO.idade,
            chipado = cadastroPetDTO.chipado,
            vacinado = cadastroPetDTO.vacinado,
            castrado = cadastroPetDTO.castrado,
            descricao = cadastroPetDTO.descricao,
            situacao = cadastroPetDTO.situacao,
            datadodesaparecimento = cadastroPetDTO.datadodesaparecimento,
            bairro = cadastroPetDTO.bairro,
            cidade = cadastroPetDTO.cidade,
            endereco= cadastroPetDTO.endereco,
            estado=cadastroPetDTO.estado,
            nomeTutor = cadastroPetDTO.nomeTutor,
            email = cadastroPetDTO.email,
            celular = cadastroPetDTO.celular,
            imagens = cadastroPetDTO.imagens?.map {
                ImagemPet(id = it.id, caminhoImagem = it.caminhoImagem, nomeArquivo = it.nomeArquivo, tipo = it.tipo) }
        )
    }

    fun toCadastroPetDTO(cadastroPet: CadastroPet):CadastroPetDTO{
        return CadastroPetDTO(
            id = cadastroPet.id,
            nomeAnimal = cadastroPet.nomeAnimal,
            tipo = cadastroPet.tipo,
            raca = cadastroPet.raca,
            idade = cadastroPet.idade,
            chipado = cadastroPet.chipado,
            vacinado = cadastroPet.vacinado,
            castrado = cadastroPet.castrado,
            descricao = cadastroPet.descricao,
            situacao = cadastroPet.situacao,
            datadodesaparecimento = cadastroPet.datadodesaparecimento,
            bairro = cadastroPet.bairro,
            endereco= cadastroPet.endereco,
            cidade = cadastroPet.cidade,
            estado=cadastroPet.estado,
            nomeTutor = cadastroPet.nomeTutor,
            email = cadastroPet.email,
            celular = cadastroPet.celular,
            imagens = cadastroPet.imagens?.map {
                ImagemPetDTO(id = it.id, caminhoImagem = it.caminhoImagem,  nomeArquivo = it.nomeArquivo, tipo = it.tipo) }
        )
    }

   fun toUsuario(usuarioDTO:UsuarioDTO):Usuario{
       return Usuario(
           id=usuarioDTO.id,
           nomeUser = usuarioDTO.nomeUser,
           celularUser = usuarioDTO.celularUser,
           emailUser = usuarioDTO.emailUser,
           senhaUser = usuarioDTO.senhaUser,
           endereco = usuarioDTO.endereco,
           bairro=usuarioDTO.bairro,
           cep= usuarioDTO.cep,
           cidade=usuarioDTO.cidade,
           estado=usuarioDTO.estado,
           codrecover = usuarioDTO.codrecover,

       )
   }
    fun toUsuarioDTO(usuario:Usuario):UsuarioDTO{
        return UsuarioDTO(
            id=usuario.id,
            nomeUser = usuario.nomeUser,
            celularUser = usuario.celularUser,
            emailUser = usuario.emailUser,
            senhaUser = usuario.senhaUser,
            endereco=usuario.endereco,
            bairro=usuario.bairro,
            cep= usuario.cep,
            cidade=usuario.cidade,
            estado=usuario.estado,
            codrecover = usuario.codrecover,
        )
    }
}