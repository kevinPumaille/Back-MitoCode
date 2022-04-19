package com.mitocode.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mitocode.dto.ConsultaDTO;
import com.mitocode.dto.ConsultaListaExamenDTO;
import com.mitocode.exception.ModeloNotFoundException;
import com.mitocode.model.Consulta;
import com.mitocode.model.Examen;
import com.mitocode.service.IConsultaService;


@RestController
@RequestMapping("/consultas")
public class ConsultaController {

	@Autowired
	private IConsultaService service;
	
	@Autowired
	private ModelMapper mapper;
	
	@GetMapping
	//@RequestMapping(value = "/" , method = RequestMethod.GET)
	public ResponseEntity<List<ConsultaDTO>> listar() throws Exception {
		List<ConsultaDTO> lista = service.listar().stream().map(p -> mapper.map(p, ConsultaDTO.class)).collect(Collectors.toList());
		
		return new ResponseEntity<>(lista, HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<ConsultaDTO> listarPorId(@PathVariable("id") Integer id) throws Exception {
		Consulta obj = service.listarPorId(id);
		
		if(obj == null) {
			throw new ModeloNotFoundException("ID NO ENCONTRADO " + id);
		}
						
		ConsultaDTO dto = mapper.map(obj, ConsultaDTO.class);
		
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}
	
	/*@PostMapping
	public ResponseEntity<Consulta> registrar(@RequestBody Consulta p) throws Exception {
		Consulta obj = service.registrar(p);
		return new ResponseEntity<>(obj, HttpStatus.CREATED);
	}*/
	
	@PostMapping
	public ResponseEntity<Void> registrar(@Valid @RequestBody ConsultaListaExamenDTO dto) throws Exception {
		Consulta c = mapper.map(dto.getConsulta(), Consulta.class);
		List<Examen> examenes = mapper.map(dto.getLstExamen(), new TypeToken<List<Examen>>() {}.getType());
		
		Consulta obj = service.registrarTransaccional(c, examenes);
		
		//localhost:8080/pacientes/5
		//URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdConsulta()).toUri();
		return ResponseEntity.created(null).build();
	}
	
	@PutMapping
	public ResponseEntity<ConsultaDTO> modificar(@Valid @RequestBody ConsultaDTO dto) throws Exception {
		Consulta obj = service.listarPorId(dto.getIdConsulta());
		
		if(obj == null) {
			throw new ModeloNotFoundException("ID NO ENCONTRADO " + dto.getIdConsulta());
		}
		
		Consulta p = mapper.map(dto, Consulta.class);		
		Consulta pac = service.modificar(p);
		ConsultaDTO dtoResponse = mapper.map(pac, ConsultaDTO.class);
		return new ResponseEntity<>(dtoResponse, HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) throws Exception {
		Consulta obj = service.listarPorId(id);
		
		if(obj == null) {
			throw new ModeloNotFoundException("ID NO ENCONTRADO " + id);
		}
		service.eliminar(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	//@ResponseStatus(HttpStatus.NOT_FOUND)
	@GetMapping("/hateoas/{id}")
	public EntityModel<ConsultaDTO> listarHateoas(@PathVariable("id") Integer id) throws Exception{
		Consulta obj = service.listarPorId(id);
		
		if(obj == null) {
			throw new ModeloNotFoundException("ID NO ENCONTRADO " + id);
		}
		
		ConsultaDTO dto = mapper.map(obj, ConsultaDTO.class);
		
		EntityModel<ConsultaDTO> recurso = EntityModel.of(dto);
		//localhost:8080/pacientes/1
		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).listarPorId(id));		
		recurso.add(link1.withRel("examen-info"));		
		return recurso;

	}
	
}
