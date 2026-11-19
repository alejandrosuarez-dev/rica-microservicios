package com.rica.publicacionesservice.publicaciones;

import com.rica.publicacionesservice.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PublicacionService {

    private final PublicacionRepository publicacionRepository;
    private final VerificadorInvestigador verificadorInvestigador;
    private final LimitePublicacionesAnualesService limitePublicacionesAnualesService;
    private final PublicadorEventosPublicaciones publicadorEventos;

    public PublicacionService(
            PublicacionRepository publicacionRepository,
            VerificadorInvestigador verificadorInvestigador,
            LimitePublicacionesAnualesService limitePublicacionesAnualesService,
            PublicadorEventosPublicaciones publicadorEventos
    ) {
        this.publicacionRepository = publicacionRepository;
        this.verificadorInvestigador = verificadorInvestigador;
        this.limitePublicacionesAnualesService = limitePublicacionesAnualesService;
        this.publicadorEventos = publicadorEventos;
    }

    public Publicacion registrar(Publicacion publicacion) {

        boolean existeInvestigador = verificadorInvestigador.existe(
                publicacion.getInvestigadorCorreo()
        );

        if (!existeInvestigador) {
            throw new RecursoNoEncontradoException(
                    "No existe un investigador con correo "
                            + publicacion.getInvestigadorCorreo()
            );
        }

        if (!limitePublicacionesAnualesService.puedeRegistrar(
                publicacion.getInvestigadorCorreo(),
                publicacion
        )) {
            throw new LimiteAnualExcedidoException(
                    "El investigador "
                            + publicacion.getInvestigadorCorreo()
                            + " ha excedido el límite anual de publicaciones"
            );
        }

        Publicacion guardada = publicacionRepository.save(publicacion);
        publicadorEventos.publicacionRegistrada(guardada);
        return guardada;
    }

    public List<Publicacion> listarPorInvestigador(String investigadorCorreo) {
        return publicacionRepository.findByInvestigadorCorreo(investigadorCorreo);
    }

    public Publicacion buscarPorId(String id) {
        return publicacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe una publicación con id " + id
                ));
    }
}