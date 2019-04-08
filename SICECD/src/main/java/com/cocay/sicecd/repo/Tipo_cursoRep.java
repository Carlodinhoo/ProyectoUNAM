package com.cocay.sicecd.repo;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.cocay.sicecd.model.TestClass;
import com.cocay.sicecd.model.Tipo_curso;

@Repository
public interface Tipo_cursoRep extends PagingAndSortingRepository<Tipo_curso, Integer>{
	List<Tipo_curso> findByNombre(String name);

}
