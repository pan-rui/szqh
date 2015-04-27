package com.yjf.yrd.front.controller.app.newVers.e8Trunck.v2;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yjf.common.lang.util.StringUtil;
import com.yjf.common.log.Logger;
import com.yjf.common.log.LoggerFactory;
import com.yjf.yrd.base.BaseAutowiredController;
import com.yjf.yrd.web.util.FileUploadUtils;

@Controller
@RequestMapping("app")
public class AppfileUploadController extends BaseAutowiredController {
	private static final Logger logger = LoggerFactory
			.getLogger(AppfileUploadController.class);

	private static List<String> fileTypeList = new ArrayList<String>();
	static {
		fileTypeList.add(".jpg");
		fileTypeList.add(".jpeg");
		fileTypeList.add(".bmp");
		fileTypeList.add(".png");
	};

	@ResponseBody
	@RequestMapping(value = "imagesUpload.htm", produces = "text/json")
	public Object imagesUpload(HttpServletRequest request,
			HttpServletResponse response, ModelMap modelMap) {
		JSONObject json = new JSONObject();
		String[] pathArray = null;
		try {
			ServletFileUpload fileUpload = new ServletFileUpload(
					new DiskFileItemFactory());
			fileUpload.setHeaderEncoding("utf-8");
			List<FileItem> fileList = null;
			try {
				fileList = fileUpload.parseRequest(request);
			} catch (FileUploadException ex) {
				logger.error(ex.getMessage(), ex);
				json.put("code", 0);
				json.put("message", "文件接收异常");
				return json;
			}
			Iterator<FileItem> it = fileList.iterator();
			String name = "";
			String extName = "";
			while (it.hasNext()) {
				FileItem item = it.next();
				if (!item.isFormField()) {
					// 解析文件
					name = item.getName();
					if (name == null || name.trim().equals("")) {
						continue;
					}
					// 得到文件的扩展名
					if (name.lastIndexOf(".") >= 0) {
						extName = name.substring(name.lastIndexOf("."));
					}
					if (!fileTypeList.contains(extName.toLowerCase())) {
						json.put("code", "1");
						json.put("message", "文件上传失败(文件类型不正确)！");
						return json;
					}
					File file = null;

					if (".pdf".equalsIgnoreCase(extName)) {
						pathArray = FileUploadUtils.getStaticFilesPdfPath(
								request, name);
					} else {
						pathArray = FileUploadUtils.getStaticFilesImgPath(
								request, name);
					}
					String savePath = pathArray[0];

					try {
						file = new File(savePath);
						item.write(file);
						if (".jpg".equalsIgnoreCase(extName)
								|| ".bmp".equalsIgnoreCase(extName)
								|| ".png".equalsIgnoreCase(extName)) {
							boolean pass = this.compressPic(savePath, savePath);
							if (!pass) {
								logger.info("文件压缩异常");
								json.put("code", 0);
								json.put("message", "文件压缩异常");
								return json;
							}
						}
					} catch (Exception e) {

						logger.error("文件写入异常，异常信息：{}", e.toString(), e);
						json.put("code", 0);
						json.put("message", "文件写入异常");
						return json;
					}
				}
			}
		} catch (Exception e) {
			logger.error("文件上传异常，异常信息：{}", e.toString(), e);
			json.put("code", 0);
			json.put("message", "文件上传异常");
			return json;
		}
		json.put("code", 1);
		json.put("message", "文件上传成功");
		json.put("serverPath", pathArray[1]);
		return json;
	}

	public boolean compressPic(String srcFilePath, String descFilePath) {
		ImageIO.setUseCache(false);
		File file = null;
		BufferedImage src = null;
		FileOutputStream out = null;
		ImageWriter imgWrier;
		ImageWriteParam imgWriteParams;

		// 指定写图片的方式为 jpg
		imgWrier = ImageIO.getImageWritersByFormatName("jpg").next();
		imgWriteParams = new javax.imageio.plugins.jpeg.JPEGImageWriteParam(
				null);
		// 要使用压缩，必须指定压缩方式为MODE_EXPLICIT
		imgWriteParams.setCompressionMode(imgWriteParams.MODE_EXPLICIT);
		// 这里指定压缩的程度，参数qality是取值0~1范围内，
		imgWriteParams.setCompressionQuality(1);
		imgWriteParams.setProgressiveMode(imgWriteParams.MODE_DISABLED);
		ColorModel colorModel = ColorModel.getRGBdefault();
		// 指定压缩时使用的色彩模式
		imgWriteParams.setDestinationType(new javax.imageio.ImageTypeSpecifier(
				colorModel, colorModel.createCompatibleSampleModel(16, 16)));

		try {
			if (StringUtil.isBlank(srcFilePath)) {
				return false;
			} else {
				file = new File(srcFilePath);
				src = ImageIO.read(file);
				out = new FileOutputStream(descFilePath);

				imgWrier.reset();
				// 必须先指定 out值，才能调用write方法, ImageOutputStream可以通过任何
				// OutputStream构造
				imgWrier.setOutput(ImageIO.createImageOutputStream(out));
				// 调用write方法，就可以向输入流写图片
				imgWrier.write(null, new IIOImage(src, null, null),
						imgWriteParams);
				out.flush();
				out.close();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}
}
