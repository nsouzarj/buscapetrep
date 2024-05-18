package eti.buscapet.buscapet.repository

import eti.buscapet.buscapet.domain.Usuario
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UsuarioRepository:JpaRepository<Usuario,Long> {
    @Query("SELECT u FROM Usuario u WHERE u.emailUser = ?1")
    fun findUserByEmail(email: String): Usuario?

}