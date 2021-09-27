package com.kame.springboot;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.kame.springboot.service.PhotoService;

@Controller
public class PhotoDisplayController {
	
	@Autowired
	PhotoService photoService;
	
	// nullの場合もあるので　required = false　をつけて、nullも受けつけるようにしておく
	@RequestMapping(value = "/getImg", method = RequestMethod.GET)
	public void getImg(@RequestParam(name = "photoId", required = false)int photoId, HttpServletResponse response) {
		
		//HttpServletResponse response　を使って、戻り値を voidにすれば、直接レスポンスを書き込むことができる。
		// photoid は、imgタグのリンクのURLの末尾にクエリー文字列として、送ってきてます URLにクエリーパラメータを指定することで、
		// コントローラメソッドで、@RequestParam 指定された値を取得することができます。
		byte[] photoData = photoService.getPhotoData(photoId); 
		String mime = photoService.getMime(photoId); // コンテンツタイプの取得  "image/jpeg"  "image/png" など "タイプ/サブタイプ" という形
		
		if(photoData != null) {
			try {
				ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
				byteOutStream.write(photoData);
				response.setContentType(mime);
				OutputStream out = response.getOutputStream();
				out.write(byteOutStream.toByteArray());  // byte[] に変換して書き込む
				out.flush();
				out.close();  
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		
	}

}
