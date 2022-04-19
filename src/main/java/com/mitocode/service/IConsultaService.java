package com.mitocode.service;

import java.util.List;

import com.mitocode.model.Consulta;
import com.mitocode.model.Examen;

public interface IConsultaService extends ICRUD<Consulta, Integer> {
		
	Consulta registrarTransaccional(Consulta consulta, List<Examen> examenes) throws Exception;
}
