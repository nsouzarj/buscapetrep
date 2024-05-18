package eti.buscapet.buscapet.domain
import jakarta.persistence.*
import lombok.*
import java.util.*

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "tbcadpet")
class CadastroPet(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var nomeAnimal: String? = null,
    var tipo: String? = null,
    var raca: String? = null,
    var idade: String? = null,
    var chipado: Boolean = false,
    var vacinado: Boolean = false,
    var castrado: Boolean = false,
    var descricao: String? = null,
    var situacao:String? = null,
    var datadodesaparecimento: Date? = null,
    var endereco: String? = null,
    var bairro: String? = null,
    var cidade: String? = null,
    var estado:String? = null,
    var nomeTutor: String? = null,
    var email: String? = null,
    var celular: String? = null,
    @OneToMany(cascade = arrayOf(CascadeType.ALL))
    var imagens: List<ImagemPet>? = null
)
