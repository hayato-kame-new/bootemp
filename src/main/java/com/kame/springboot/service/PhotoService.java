package com.kame.springboot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kame.springboot.repositories.PhotoRepository;

@Service
@Transactional
public class PhotoService {
	
	@Autowired
	PhotoRepository photorepository;
	
	

}
