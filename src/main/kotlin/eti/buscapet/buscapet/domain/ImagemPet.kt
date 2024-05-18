package eti.buscapet.buscapet.domain
import jakarta.persistence.*
import lombok.*

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "tbpetimagens")
class ImagemPet(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var caminhoImagem: String? =null,
    var nomeArquivo: String?=null,
    var tipo: String?=null
)