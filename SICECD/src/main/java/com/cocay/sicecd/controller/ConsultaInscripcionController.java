package com.cocay.sicecd.controller;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cocay.sicecd.model.Curso;
import com.cocay.sicecd.model.Grupo;
import com.cocay.sicecd.model.Inscripcion;
import com.cocay.sicecd.model.Profesor;
import com.cocay.sicecd.repo.CursoRep;
import com.cocay.sicecd.repo.GrupoRep;
import com.cocay.sicecd.repo.InscripcionRep;
import com.cocay.sicecd.repo.ProfesorRep;


@Controller
public class ConsultaInscripcionController {
	@Autowired
	InscripcionRep ins_rep;
	
	@Autowired
	ProfesorRep profesor_rep;
	
	@Autowired
	CursoRep curso_rep;
	
	@Autowired
	GrupoRep grupo_rep;
	
	@RequestMapping(value = "/consultaInscripcion", method = RequestMethod.GET)
	public String consultaCurso(Model model) {
		return "ConsultarInscripcion/consultaInscripcion";
	}
	
	/**
	 * Busca una la lista de inscripciones de acuerdo a los datos ingresados por el usuario.
	 * @param model
	 * @param request
	 * @return
	 * @throws ParseException
	 */
	@RequestMapping(value = "/consultaInscripciones", method = RequestMethod.POST)
	public ModelAndView consultaInscripciones(ModelMap model,HttpServletRequest request) throws ParseException {
		
		/* Datos del profesor */
		String curp = request.getParameter("curp").toUpperCase().trim();
		String rfc = request.getParameter("rfc").toUpperCase().trim();
		String nombre = normalizar(request.getParameter("nombre")).toUpperCase().trim();
		String apellido_paterno = normalizar(request.getParameter("apellido_paterno")).toUpperCase().trim();
		Integer id_grado = Integer.parseInt(request.getParameter("grado_estudios"));
		Integer id_genero = Integer.parseInt(request.getParameter("genero"));
		Integer id_turno = Integer.parseInt(request.getParameter("turno"));
		
		/* Datos del curso */
		String clave_curso = request.getParameter("clave_curso").trim();
		Integer id_tipo = Integer.parseInt(request.getParameter("tipos"));
		
		/* Datos del grupo */
		String clave_grupo = request.getParameter("clave_grupo").trim();
		
		/* Intervalo de tiempo */
		String fecha_inicio_1 = request.getParameter("fecha_1");
		String fecha_inicio_2 = request.getParameter("fecha_2");
		
		List<Inscripcion> ins_grupos = null;
		List<Inscripcion> ins_profes = null;
		List<Inscripcion> ins_cursos = null;
		
		if (rfc != "" || curp != "" || nombre != "" || apellido_paterno != "" || id_grado != 5 || id_genero != 3 || id_turno != 4) {
			ins_profes = obtenerInsProfes(rfc, curp, nombre, apellido_paterno, id_grado, id_genero, id_turno);
		}
		
		if ( clave_grupo != "" ) {
			ins_grupos = obtenerInsGrupos(clave_grupo);
		}
		
		if ( clave_curso != "" || id_tipo != 4 || fecha_inicio_1 != null || fecha_inicio_2 != null) {
			ins_cursos = obtenerInsCursos(clave_curso, id_tipo, fecha_inicio_1, fecha_inicio_2);
		}
		
		//Merge entre cursos, grupos y profes
		//List<Inscripcion> inscripciones = obtenerIns(ins_cursos, ins_grupos, ins_profes);
		List<Inscripcion> inscripciones = obtenerIns(ins_grupos, ins_profes);
		
		if ( inscripciones != null || inscripciones.size() > 0 ) {
			model.put("ins", inscripciones);
			return new ModelAndView("ConsultarInscripcion/muestraListaIns", model);
		} else {
			return new ModelAndView("/Avisos/ErrorBusqueda");
		}
	}
	
	/**
	 * Realiza una intersección de las inscripciones encontradas en sección de profesores y grupos. 
	 * @param ins_grupos Inscripciones encontradas en la sección grupos.
	 * @param ins_profes Inscripciones encontradas en la sección grupos.
	 * @return una lista de las inscripciones que se encuentran en la lista de profes y grupos.
	 */
	public List<Inscripcion> obtenerIns (List<Inscripcion> ins_grupos, List<Inscripcion> ins_profes) {
		List<Inscripcion> inscripciones = new ArrayList<Inscripcion>();
		
		if( ins_grupos != null && ins_profes == null ) {
			return ins_grupos;
		} else if( ins_grupos == null && ins_profes != null ) {
			return ins_profes;
		}else {
			for (Inscripcion ins : ins_grupos) {
				if ( ins_profes.contains(ins) ) {
					inscripciones.add(ins);
				}
			}
		}
		
		return inscripciones;
	}
	
	/**
	 * Se busca la lista de inscripciones de acuerdo tomando como parámetros
	 * los datos ingresados en la sección de 'Cursos'.
	 * @param clave_curso
	 * @param id_tipo
	 * @param fecha_inicio_1
	 * @param fecha_inicio_2
	 * @return una lista de Inscripciones.
	 * @throws ParseException
	 */
	public List<Inscripcion> obtenerInsCursos(String clave_curso, Integer id_tipo, String fecha_inicio_1, String fecha_inicio_2) throws ParseException {
		System.out.println("Entrando a obtenerInsCursos");
		List<Inscripcion> ins_cursos = new ArrayList<Inscripcion>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date fecha_ini_1, fecha_ini_2;
		List<Curso> cursos1, cursos2;
		
		if(fecha_inicio_1 != "" && fecha_inicio_2 != "") {
			fecha_ini_1 = format.parse(fecha_inicio_1);
			fecha_ini_2 = format.parse(fecha_inicio_2);
			cursos1 = curso_rep.findByFechaInicio(fecha_ini_1, fecha_ini_2);
			cursos2 = curso_rep.findByFechaInicio(fecha_ini_1, fecha_ini_2);
		} else if(fecha_inicio_1 != "" && fecha_inicio_2 == "") {
			fecha_ini_1 = format.parse(fecha_inicio_1);
			cursos1 = curso_rep.findByFechaInicio(fecha_ini_1);
			cursos2 = curso_rep.findByFechaInicio(fecha_ini_1);
		} else {
			cursos1 = curso_rep.findAll();
			cursos2 = curso_rep.findAll();
		}
		
		//Filtrando por tipo de curso
		if (id_tipo != 4) {
			for(Curso c : cursos1) {
				if(c.getFk_id_tipo_curso().getPk_id_tipo_curso() != id_tipo ) {
					cursos2.remove(c);
				}
			}
		}
		
		//Filtrando por clave de curso
		if (clave_curso != "") {
			for(Curso c : cursos1) {
				if(!c.getClave().contains(clave_curso)){
					cursos2.remove(c);
				}
			}
		}
		
		for (Curso c : cursos2) {
			List<Grupo> grupos = c.getGrupos();
			for (Grupo g : grupos) {
				ins_cursos.addAll(g.getInscripciones());
			}
		}
		
		System.out.println(ins_cursos.size());
		return ins_cursos;
	}
	
	/**
	 * Se busca la lista de inscripciones de acuerdo tomando como parámetros
	 * los datos ingresados en la sección de 'Grupos'.
	 * @param clave
	 * @return una lista de Inscripciones.
	 */
	public List<Inscripcion> obtenerInsGrupos(String clave) {
		List<Inscripcion> ins_grupos = new ArrayList<Inscripcion>();
		List<Grupo> grupos = grupo_rep.findByClave(clave);
		
		for (Grupo g : grupos) {
			ins_grupos.addAll(g.getInscripciones());
		}
		
		return ins_grupos;
	}
	
	/**
	 * Se busca la lista de inscripciones de acuerdo tomando como parámetros
	 * los datos ingresados en la sección de 'Profesores'.
	 * @param rfc
	 * @param curp
	 * @param nombre
	 * @param apellido_paterno
	 * @param id_grado
	 * @param id_genero
	 * @param id_turno
	 * @return una lista de Inscripciones
	 * @throws ParseException
	 */
	public List<Inscripcion> obtenerInsProfes(String rfc, String curp, String nombre, String apellido_paterno, Integer id_grado, Integer id_genero, Integer id_turno) throws ParseException {
		List<Inscripcion> ins_profes = new ArrayList<Inscripcion>();
		List<Profesor> profes1 = profesor_rep.findAll();
		List<Profesor> profes2 = profesor_rep.findAll();
		
		//Caso: Búsqueda por RFC
		if (rfc != "" ) {
			Profesor p = profesor_rep.findByRfc(rfc);
			ins_profes.addAll(p.getInscripciones());
		//Caso: Búsqueda por CURP
		} else if (curp != "") {
			Profesor p = profesor_rep.findByCurp(curp);
			ins_profes.addAll(p.getInscripciones());
		//Caso: Búsqueda por los filtros restantes
		} else {
			//Filtrando por Nombre
			if (nombre != "") {
				for (Profesor p : profes1) {
					String nom = normalizar(p.getNombre()).toUpperCase().trim();
					if( !nom.contains(nombre) ) {
						profes2.remove(p);
					}
				}
			}
			
			//Filtrando por Apellido Paterno
			if (apellido_paterno != "") {
				for (Profesor p : profes1) {
					String ap = normalizar(p.getApellido_paterno()).toUpperCase().trim();
					if( !ap.contains(apellido_paterno) ) {
						profes2.remove(p);
					}
				}
			}
									
			//Filtrando por grado de estudios
			if (id_grado != 5) {
				for(Profesor p : profes1) {
					if(p.getFk_id_grado_profesor().getPk_id_grado_profesor() != id_grado) {
						profes2.remove(p);
					}
				}
			}
						
			//Filtrando por género
			if ( id_genero != 3) {
				for(Profesor p : profes1) {
					if(p.getId_genero().getPk_id_genero() != id_genero) {
						profes2.remove(p);
					}
				}
			}
			
			//Filtrando por turno
			if( id_turno != 4) {
				for(Profesor p : profes1) {
					if(p.getFk_id_turno().getPk_id_turno() != id_turno) {
						profes2.remove(p);
					}
				}
			}
			
			//Se agregan las inscripciones de cada uno de los profesores
			for (Profesor p : profes2) {
				ins_profes.addAll(p.getInscripciones());
			}
		}
		
		return ins_profes;
	}
	
	public String normalizar(String src) {
        return Normalizer
                .normalize(src , Normalizer.Form.NFD)
                .replaceAll("[^\\p{ASCII}]" , "");
    }
}
