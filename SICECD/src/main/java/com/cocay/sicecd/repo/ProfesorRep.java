package com.cocay.sicecd.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cocay.sicecd.model.Profesor;


@Repository
public interface ProfesorRep extends PagingAndSortingRepository<Profesor, Integer>{

@Query("SELECT p FROM Profesor p  where p.rfc = :rfc ")
Profesor findByRfc(@Param("rfc")String rfc);

@Query("SELECT p FROM Profesor p "
		+ "WHERE upper(p.nombre) LIKE CONCAT('%',:nombre,'%') "
		+ "AND upper(p.apellido_paterno) LIKE CONCAT('%',:apellido_paterno,'%') "
		+ "AND upper(p.apellido_materno) LIKE CONCAT('%',:apellido_materno,'%')")
Profesor findByCompleteName(@Param("nombre") String nombre,
                             @Param("apellido_paterno") String apellido_paterno,
                             @Param("apellido_materno") String apellido_materno);

@Query("SELECT p FROM Profesor p "
		+ "WHERE upper(p.nombre) LIKE CONCAT('%',:nombre,'%') "
		+ "AND upper(p.apellido_paterno) LIKE CONCAT('%',:apellido_paterno,'%') "
		+ "AND upper(p.apellido_materno) LIKE CONCAT('%',:apellido_materno,'%')")
List<Profesor> findByCompleteNameList(@Param("nombre") String nombre,
                         @Param("apellido_paterno") String apellido_paterno,
                         @Param("apellido_materno") String apellido_materno);

@Query("SELECT p FROM Profesor p "
		+ "WHERE p.fk_id_estado = :estado ")
List<Profesor> findByStateList(@Param("estado") int estado);

@Query("SELECT p FROM Profesor p "
		+ "WHERE upper(p.nombre) LIKE CONCAT('%',:nombre,'%') "
		+ "AND upper(p.apellido_paterno) LIKE CONCAT('%',:apellido_paterno,'%')"
		+ "AND upper(p.apellido_materno) LIKE CONCAT('%',:apellido_materno,'%')"
		+ "AND upper(p.id_genero) LIKE CONCAT('%',:genero,'%')")
List<Profesor> findByAllList(@Param("nombre") String nombre,
                         @Param("apellido_paterno") String apellido_paterno,
                         @Param("apellido_materno") String apellido_materno,
                         @Param("genero") String genero);



}
	
	
