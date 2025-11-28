package com.semanticshop.service;

import com.semanticshop.model.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.semanticweb.owlapi.model.*;
import org.springframework.stereotype.Service;

/**
 * Servicio para sincronizar usuarios de la base de datos con la ontología
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OntologySyncService {

    private final OntologyService ontologyService;

    /**
     * Sincroniza un usuario de la BD con la ontología
     */
    public void sincronizarUsuarioConOntologia(Usuario usuario) {
        try {
            String clienteId = usuario.getClienteIdOntologia();
            
            if (clienteId == null) {
                log.warn("Usuario {} no tiene clienteIdOntologia", usuario.getUsername());
                return;
            }

            log.info("Sincronizando usuario {} con ontología como {}", usuario.getUsername(), clienteId);

            OWLDataFactory factory = ontologyService.getDataFactory();
            OWLOntology ontology = ontologyService.getOntology();
            OWLOntologyManager manager = ontology.getOWLOntologyManager();
            String namespace = ontologyService.getNamespace();

            // 1. Crear el individuo Cliente
            OWLNamedIndividual cliente = factory.getOWLNamedIndividual(
                IRI.create(namespace + clienteId)
            );

            // 2. Asignar a la clase Cliente
            OWLClass claseCliente = factory.getOWLClass(IRI.create(namespace + "Cliente"));
            OWLClassAssertionAxiom axiomClase = factory.getOWLClassAssertionAxiom(claseCliente, cliente);
            manager.addAxiom(ontology, axiomClase);

            // 3. Agregar propiedad nombre
            OWLDataProperty propNombre = factory.getOWLDataProperty(IRI.create(namespace + "nombre"));
            OWLLiteral literalNombre = factory.getOWLLiteral(
                usuario.getNombreCompleto() != null ? usuario.getNombreCompleto() : usuario.getUsername()
            );
            OWLDataPropertyAssertionAxiom axiomNombre = factory.getOWLDataPropertyAssertionAxiom(
                propNombre, cliente, literalNombre
            );
            manager.addAxiom(ontology, axiomNombre);

            // 4. Agregar marca preferida si existe
            if (usuario.getMarcaPreferida() != null) {
                OWLObjectProperty propMarca = factory.getOWLObjectProperty(
                    IRI.create(namespace + "tieneMarcaPreferida")
                );
                
                // Usar el nombre exacto de la marca como está en la ontología
                String marcaId = usuario.getMarcaPreferida(); // "Apple", "Samsung", etc.
                OWLNamedIndividual marca = factory.getOWLNamedIndividual(
                    IRI.create(namespace + marcaId)
                );
                
                // Asignar la marca a la clase Marca
                OWLClass claseMarca = factory.getOWLClass(IRI.create(namespace + "Marca"));
                manager.addAxiom(ontology, factory.getOWLClassAssertionAxiom(claseMarca, marca));
                
                // Relacionar cliente con marca
                OWLObjectPropertyAssertionAxiom axiomMarca = factory.getOWLObjectPropertyAssertionAxiom(
                    propMarca, cliente, marca
                );
                manager.addAxiom(ontology, axiomMarca);
                
                log.info("Marca preferida agregada: {}", marcaId);
            }

            // 5. Agregar sistema operativo preferido si existe
            if (usuario.getSistemaOperativoPreferido() != null) {
                OWLObjectProperty propSO = factory.getOWLObjectProperty(
                    IRI.create(namespace + "tienePreferencia")
                );
                
                // Usar el nombre exacto del SO como está en la ontología
                String soId = usuario.getSistemaOperativoPreferido(); // "iOS", "Android", "Windows", "MacOS"
                OWLNamedIndividual so = factory.getOWLNamedIndividual(
                    IRI.create(namespace + soId)
                );
                
                // Asignar a la clase SistemaOperativo
                OWLClass claseSO = factory.getOWLClass(IRI.create(namespace + "SistemaOperativo"));
                manager.addAxiom(ontology, factory.getOWLClassAssertionAxiom(claseSO, so));
                
                OWLObjectPropertyAssertionAxiom axiomSO = factory.getOWLObjectPropertyAssertionAxiom(
                    propSO, cliente, so
                );
                manager.addAxiom(ontology, axiomSO);
                
                log.info("Sistema operativo agregado: {}", soId);
            }

            // 6. Agregar rango de precio si existe
            if (usuario.getRangoPrecioMin() != null) {
                OWLDataProperty propPrecioMin = factory.getOWLDataProperty(
                    IRI.create(namespace + "presupuestoMinimo")
                );
                OWLLiteral literalPrecioMin = factory.getOWLLiteral(usuario.getRangoPrecioMin());
                manager.addAxiom(ontology, factory.getOWLDataPropertyAssertionAxiom(
                    propPrecioMin, cliente, literalPrecioMin
                ));
            }

            if (usuario.getRangoPrecioMax() != null) {
                OWLDataProperty propPrecioMax = factory.getOWLDataProperty(
                    IRI.create(namespace + "presupuestoMaximo")
                );
                OWLLiteral literalPrecioMax = factory.getOWLLiteral(usuario.getRangoPrecioMax());
                manager.addAxiom(ontology, factory.getOWLDataPropertyAssertionAxiom(
                    propPrecioMax, cliente, literalPrecioMax
                ));
            }

            // 7. Ejecutar el razonador para inferir nuevos conocimientos
            ontologyService.runReasoner();

            log.info("✅ Usuario {} sincronizado exitosamente con la ontología", clienteId);

        } catch (Exception e) {
            log.error("❌ Error al sincronizar usuario con ontología: {}", e.getMessage(), e);
            throw new RuntimeException("Error al sincronizar con ontología", e);
        }
    }

    /**
     * Verifica si un cliente existe en la ontología
     */
    public boolean clienteExisteEnOntologia(String clienteId) {
        try {
            return ontologyService.getIndividualsOfClass("Cliente").stream()
                .anyMatch(ind -> ind.getIRI().getShortForm().equals(clienteId));
        } catch (Exception e) {
            log.error("Error al verificar existencia de cliente: {}", e.getMessage());
            return false;
        }
    }
}