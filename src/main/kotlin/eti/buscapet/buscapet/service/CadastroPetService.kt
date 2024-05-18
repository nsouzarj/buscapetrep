package eti.buscapet.buscapet.service

import eti.buscapet.buscapet.domain.CadastroPet
import org.springframework.stereotype.Service
import eti.buscapet.buscapet.repository.CadastroPetRepository

@Service
class CadastroPetService(private val repository: CadastroPetRepository) {
    fun salvar(cadastroPet: CadastroPet): CadastroPet {
        return repository.save(cadastroPet)
    }

    fun buscarPorId(id: Long): CadastroPet? {
        return repository.findById(id).orElse(null)
    }

    fun listarTodos(): List<CadastroPet?>? {
        return repository.findAllByOrderByDataDesaparecimento()
    }

    fun atualizar(cadastroPet: CadastroPet): CadastroPet {
        return repository.save(cadastroPet)
    }

    fun deletar(id: Long) {
        repository.deleteById(id)
    }
}