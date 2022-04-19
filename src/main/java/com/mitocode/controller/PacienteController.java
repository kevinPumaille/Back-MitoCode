package com.mitocode.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.mitocode.dto.PacienteDTO;
import com.mitocode.exception.ModeloNotFoundException;
import com.mitocode.model.Paciente;
import com.mitocode.service.IPacienteService;


@RestController
@RequestMapping("/pacientes")
//@CrossOrigin(origins = "http://localhost:4200")
public class PacienteController {

	@Autowired
	private IPacienteService service;
	
	@Autowired
	private ModelMapper mapper;
	
	@GetMapping
	//@RequestMapping(value = "/" , method = RequestMethod.GET)
	public ResponseEntity<List<PacienteDTO>> listar() throws Exception {
		List<PacienteDTO> lista = service.listar().stream().map(p -> mapper.map(p, PacienteDTO.class)).collect(Collectors.toList());
		
		return new ResponseEntity<>(lista, HttpStatus.OK);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<PacienteDTO> listarPorId(@PathVariable("id") Integer id) throws Exception {
		Paciente obj = service.listarPorId(id);
		
		if(obj == null) {
			throw new ModeloNotFoundException("ID NO ENCONTRADO " + id);
		}
						
		PacienteDTO dto = mapper.map(obj, PacienteDTO.class);
		
		return new ResponseEntity<>(dto, HttpStatus.OK);
	}
	
	/*@PostMapping
	public ResponseEntity<Paciente> registrar(@RequestBody Paciente p) throws Exception {
		Paciente obj = service.registrar(p);
		return new ResponseEntity<>(obj, HttpStatus.CREATED);
	}*/
	
	@PostMapping
	public ResponseEntity<Void> registrar(@Valid @RequestBody PacienteDTO dto) throws Exception {
		Paciente p = mapper.map(dto, Paciente.class);
		Paciente obj = service.registrar(p);
		
		//localhost:8080/pacientes/5
		URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdPaciente()).toUri();
		return ResponseEntity.created(location).build();
	}
	
	@PutMapping
	public ResponseEntity<PacienteDTO> modificar(@Valid @RequestBody PacienteDTO dto) throws Exception {
		Paciente obj = service.listarPorId(dto.getIdPaciente());
		
		if(obj == null) {
			throw new ModeloNotFoundException("ID NO ENCONTRADO " + dto.getIdPaciente());
		}
		
		Paciente p = mapper.map(dto, Paciente.class);		
		Paciente pac = service.modificar(p);
		PacienteDTO dtoResponse = mapper.map(pac, PacienteDTO.class);
		return new ResponseEntity<>(dtoResponse, HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> eliminar(@PathVariable("id") Integer id) throws Exception {
		Paciente obj = service.listarPorId(id);
		
		if(obj == null) {
			throw new ModeloNotFoundException("ID NO ENCONTRADO " + id);
		}
		service.eliminar(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	//@ResponseStatus(HttpStatus.NOT_FOUND)
	@GetMapping("/hateoas/{id}")
	public EntityModel<PacienteDTO> listarHateoas(@PathVariable("id") Integer id) throws Exception{
		Paciente obj = service.listarPorId(id);
		
		if(obj == null) {
			throw new ModeloNotFoundException("ID NO ENCONTRADO " + id);
		}
		
		PacienteDTO dto = mapper.map(obj, PacienteDTO.class);
		
		EntityModel<PacienteDTO> recurso = EntityModel.of(dto);
		//localhost:8080/pacientes/1
		WebMvcLinkBuilder link1 = linkTo(methodOn(this.getClass()).listarPorId(id));
		WebMvcLinkBuilder link2 = linkTo(methodOn(MedicoController.class).listarPorId(id));
		recurso.add(link1.withRel("paciente-info"));
		recurso.add(link2.withRel("medico-info"));
		return recurso;

	}
	
}
