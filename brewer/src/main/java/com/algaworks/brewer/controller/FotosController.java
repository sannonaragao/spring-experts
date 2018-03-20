package com.algaworks.brewer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.algaworks.brewer.dto.FotoDTO;
import com.algaworks.brewer.storage.FotoStorage;
import com.algaworks.brewer.storage.FotoStorageRunnable;

@RestController  //anotar a classe coo @RestController, não @Controller, o retorno automaticamente vira @ResponseBody.
@RequestMapping("/fotos")
public class FotosController {

	@Autowired
	private FotoStorage fotoStorage;
	
	@PostMapping //@PostMapping ( que tem mesma função de @ResquestMapping(method=RequestMethod.POST), só que é mais direta.
	public DeferredResult<FotoDTO> upload(@RequestParam("files[]") MultipartFile[] files	) {
		DeferredResult<FotoDTO> resultado = new DeferredResult<FotoDTO>();
		
		Thread thread = new Thread(new FotoStorageRunnable(files, resultado, fotoStorage));
		thread.start();
		
//		System.out.println(files[0].getSize());
		
		return resultado;
	}
	
//	@GetMapping("/temp/{nomeFoto:.*}")
//	public byte[] recuperarFotoTemporária(@PathVariable("nomeFoto") String nomeFoto){
//		return fotoStorage.recuperarFotoTemporária(nomeFoto);
//	}
	
	@GetMapping("/{nomeFoto:.*}")
	public byte[] recuperar(@PathVariable("nomeFoto") String nomeFoto){
		return fotoStorage.recuperar(nomeFoto);
	}
	
}

/*
Request Payload
------WebKitFormBoundaryfbBaPNMNaWYc0PW8
Content-Disposition: form-data; name="files[]"; filename="673xfk.jpg"
Content-Type: image/jpeg

------WebKitFormBoundaryfbBaPNMNaWYc0PW8--
*/