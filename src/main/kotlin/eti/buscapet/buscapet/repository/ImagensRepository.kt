package eti.buscapet.buscapet.repository
import eti.buscapet.buscapet.domain.ImagemPet
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ImagensRepository: JpaRepository<ImagemPet, Long> {}
