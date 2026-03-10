package com.adorno.repositories;

import com.adorno.model.entities.Movil;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovilRepository extends JpaRepository<Movil, Integer>, JpaSpecificationExecutor<Movil> {

    /**
     * Los 5 móviles con más consultas (campo `consultas` de la tabla).
     */
    @Query("SELECT m FROM Movil m ORDER BY m.consultas DESC")
    List<Movil> findTop5ByConsultas(Pageable pageable);

    /**
     * Marcas disponibles sin repeticiones.
     */
    @Query("SELECT DISTINCT m.marca FROM Movil m ORDER BY m.marca ASC")
    List<String> findDistinctMarcas();

    /**
     * Tecnologías de pantalla disponibles.
     */
    @Query("SELECT DISTINCT m.pantallaTecnologia FROM Movil m WHERE m.pantallaTecnologia IS NOT NULL ORDER BY m.pantallaTecnologia ASC")
    List<String> findDistinctTecnologias();

    /**
     * Incrementa el contador `consultas` de un móvil.
     */
    @Modifying
    @Query("UPDATE Movil m SET m.consultas = m.consultas + 1 WHERE m.id = :id")
    void incrementarConsultas(@Param("id") Integer id);
}

