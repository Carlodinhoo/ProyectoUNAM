package com.cocay.sicecd.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.cocay.sicecd.model.Grado_profesor;

@Repository
public interface Grado_profesorRep extends PagingAndSortingRepository<Grado_profesor, Integer>{
	List<Grado_profesor> findByNombre(String name);

}
