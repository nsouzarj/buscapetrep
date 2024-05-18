package eti.buscapet.buscapet.repository
import eti.buscapet.buscapet.domain.CadastroPet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository


@Repository
interface CadastroPetRepository : JpaRepository<CadastroPet, Long>{
    @Query("select c from CadastroPet c order by c.datadodesaparecimento desc")
    fun  findAllByOrderByDataDesaparecimento(): List<CadastroPet?>?
}
