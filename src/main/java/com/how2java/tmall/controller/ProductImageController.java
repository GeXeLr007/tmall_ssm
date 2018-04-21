package com.how2java.tmall.controller;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.pojo.Product;
import com.how2java.tmall.pojo.ProductImage;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.service.ProductImageService;
import com.how2java.tmall.service.ProductService;
import com.how2java.tmall.util.ImageUtil;
import com.how2java.tmall.util.UploadedImageFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("")
public class ProductImageController {
    @Autowired
    ProductImageService productImageService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;

    @RequestMapping("admin_productImage_list")
    public String list(Integer pid, Model model) {
        Product product = productService.get(pid);
        Category category = categoryService.get(product.getCid());
        product.setCategory(category);
        List<ProductImage> pisSingle = productImageService.list(pid, ProductImageService.type_single);
        List<ProductImage> pisDetail = productImageService.list(pid, ProductImageService.type_detail);
        model.addAttribute("p", product);
        model.addAttribute("pisSingle", pisSingle);
        model.addAttribute("pisDetail", pisDetail);
        return "admin/listProductImage";
    }

    @RequestMapping("admin_productImage_add")
    public String add(ProductImage productImage, UploadedImageFile uploadedImageFile, HttpSession session) throws IOException {
        productImageService.add(productImage);
        String imageFolder = null;
        String imageFolder_middle = null;
        String imageFolder_small = null;
        String filename = productImage.getId() + ".jpg";
        if (productImage.getType().equals(ProductImageService.type_single)) {
            imageFolder = session.getServletContext().getRealPath("img/productSingle");
            imageFolder_middle = session.getServletContext().getRealPath("img/productSingle_middle");
            imageFolder_small = session.getServletContext().getRealPath("img/productSingle_small");
        } else {
            imageFolder = session.getServletContext().getRealPath("img/productDetail");
        }
        File file = new File(imageFolder, filename);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        MultipartFile image = uploadedImageFile.getImage();
        image.transferTo(file);
        BufferedImage img = ImageUtil.change2jpg(file);
        ImageIO.write(img, "jpg", file);
        if (productImage.getType().equals(ProductImageService.type_single)) {
            File file_middle = new File(imageFolder_middle, filename);
            File file_small = new File(imageFolder_small, filename);
            if (!file_middle.getParentFile().exists()) {
                file_middle.getParentFile().mkdirs();
            }
            if (!file_small.getParentFile().exists()) {
                file_small.getParentFile().mkdirs();
            }
            ImageUtil.resizeImage(file, 56, 56, file_small);
            ImageUtil.resizeImage(file, 217, 190, file_middle);
        }
        return "redirect:admin_productImage_list?pid=" + productImage.getPid();
    }

    @RequestMapping("admin_productImage_delete")
    public String delete(Integer id, HttpSession session) {
        // 获取productImage对象，用于拼接路径字符串
        ProductImage productImage = productImageService.get(id);
        String imageFolder = null;
        String imageFolder_middle = null;
        String imageFolder_small = null;
        String filename = productImage.getId() + ".jpg";
//        判断产品图片类型，若是单个图片，则还需删除中号、小号图片
        if (productImage.getType().equals(ProductImageService.type_single)) {
            imageFolder = session.getServletContext().getRealPath("img/productSingle");
            imageFolder_middle = session.getServletContext().getRealPath("img/productSingle_middle");
            imageFolder_small = session.getServletContext().getRealPath("img/productSingle_small");
        } else {
            imageFolder = session.getServletContext().getRealPath("img/productDetail");
        }
        File file = new File(imageFolder, filename);
        file.delete();
        if (productImage.getType().equals(ProductImageService.type_single)) {
            File file_middle = new File(imageFolder_middle, filename);
            File file_small = new File(imageFolder_small, filename);
            file_middle.delete();
            file_small.delete();
        }
        // productImage对象已使用完毕，可以从数据库中删除对应的记录
        productImageService.delete(id);
        return "redirect:admin_productImage_list?pid=" + productImage.getPid();
    }
}
