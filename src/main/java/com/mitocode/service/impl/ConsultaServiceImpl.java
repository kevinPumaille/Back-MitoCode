package com.mitocode.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mitocode.model.Consulta;
import com.mitocode.model.Examen;
import com.mitocode.repo.IConsultaExamenRepo;
import com.mitocode.repo.IConsultaRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.IConsultaService;

@Service
public class ConsultaServiceImpl extends CRUDImpl<Consulta, Integer> implements IConsultaService {
	
	@Autowired
	private IConsultaRepo repo;
	
	@Autowired
	private IConsultaExamenRepo ceRepo;

	@Override
	protected IGenericRepo<Consulta, Integer> getRepo() {
		return repo;
	}

	@Transactional
	@Override
	public Consulta registrarTransaccional(Consulta consulta, List<Examen> examenes) throws Exception {
		consulta.getDetalleConsulta().forEach(det -> det.setConsulta(consulta));
		
		repo.save(consulta);		
		
		examenes.forEach(e -> ceRepo.registrar(consulta.getIdConsulta(), e.getIdExamen()));
		
		return consulta;
		
	}


}
