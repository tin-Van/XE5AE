package com.poly.service;

import java.io.File;

import org.springframework.web.multipart.MultipartFile;

public interface HomeService  {

	File saveImage(MultipartFile file);

}
