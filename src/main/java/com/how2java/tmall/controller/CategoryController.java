package com.how2java.tmall.controller;

import com.how2java.tmall.pojo.Category;
import com.how2java.tmall.service.CategoryService;
import com.how2java.tmall.util.ImageUtil;
import com.how2java.tmall.util.Page;
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
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @RequestMapping("admin_category_list")
    public String list(Model model, Page page) {
        List<Category> cs = categoryService.list(page);
        int total = categoryService.total();
        page.setTotal(total);
        model.addAttribute("cs", cs);
        model.addAttribute("page", page);
        return "admin/listCategory";
    }

    @RequestMapping("admin_category_add")
    public String add(Category c, HttpSession session, UploadedImageFile uploadedImageFile) throws IOException {
        categoryService.add(c);
        File imageFolder = new File(session.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, c.getId() + ".jpg");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        uploadedImageFile.getImage().transferTo(file);
        BufferedImage img = ImageUtil.change2jpg(file);
        ImageIO.write(img, "jpg", file);
        return "redirect:/admin_category_list";
    }

    @RequestMapping("admin_category_delete")
    public String add(Integer id, HttpSession session) {
        categoryService.delete(id);
        File imgFolder = new File(session.getServletContext().getRealPath("img/category"));
        File file = new File(imgFolder, id + ".jpg");
        file.delete();
        return "redirect:/admin_category_list";

    }

    @RequestMapping("admin_category_edit")
    public String edit(Integer id, Model model) {
        Category c = categoryService.get(id);
        model.addAttribute("c", c);
        return "admin/editCategory";
    }

    @RequestMapping("admin_category_update")
    public String update(Category category, HttpSession session, UploadedImageFile uploadedImageFile) throws IOException {
        categoryService.update(category);
        MultipartFile image = uploadedImageFile.getImage();
        if (image != null && !image.isEmpty()) {
            File imgFolder = new File(session.getServletContext().getRealPath("img/category"));
            File file = new File(imgFolder, category.getId() + ".jpg");
            image.transferTo(file);
            BufferedImage img = ImageUtil.change2jpg(file);
            ImageIO.write(img,".jpg",file);
        }

        return "redirect:/admin_category_list";
    }

}
