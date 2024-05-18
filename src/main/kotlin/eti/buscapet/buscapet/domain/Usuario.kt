package eti.buscapet.buscapet.domain
import jakarta.persistence.*
import lombok.*

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "usuario")
class Usuario (

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null,

    @Column(name = "nome_user")
    var nomeUser: String? = null,

    @Column(name="endereco")
    var endereco:String?= null,

    @Column(name="bairro")
    var bairro:String?= null,

    @Column(name="cep")
    var cep:String?= null,

    @Column(name="cidade")
    var cidade:String?= null,

    @Column(name="estado")
    var estado:String?= null,

    @Column(name = "email_user")
    var emailUser: String? = null,

    @Column(name = "celular_user")
    var celularUser: String? = null,

    @Column(name = "senha_user")
    var senhaUser: String? = null,
    @Column(name="codrecover")
    var codrecover: String? =null
)