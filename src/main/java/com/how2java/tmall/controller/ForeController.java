package com.how2java.tmall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.how2java.tmall.comparator.*;
import com.how2java.tmall.pojo.*;
import com.how2java.tmall.service.*;
import org.apache.commons.lang.math.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("")
public class ForeController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    OrderService orderService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    ProductService productService;
    @Autowired
    PropertyService propertyService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    UserService userService;
    @Autowired
    ReviewService reviewService;

    @RequestMapping("forehome")
    public String home(Model model) {
        List<Category> categories = categoryService.list();
        productService.fill(categories);
        productService.fillByRow(categories);
        model.addAttribute("cs", categories);
        return "fore/home";
    }

    @RequestMapping("foreregister")
    public String register(Model model, User user) {
        String name = user.getName();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist = userService.isExist(name);

        if (exist) {
            String m = "用户名已经被使用,不能使用";
            model.addAttribute("msg", m);
            //因为如果用户名已存在，使用的是服务端转发到register.jsp,本来request中是存留着user对象的，不把user对象设置为null，将会随着request一起转发到register.jsp
            model.addAttribute("user", null);
            return "fore/register";
        }
        userService.add(user);

        return "redirect:registerSuccessPage";
    }

    @RequestMapping("forelogin")
    public String login(@RequestParam("name") String name, @RequestParam("password") String password, HttpSession session, Model model) {
        name = HtmlUtils.htmlEscape(name);
        User user = userService.get(name, password);
        if (user != null) {
            session.setAttribute("user", user);
            return "redirect:forehome";
        }
//        输入的用户名或密码有误，跳转回登录页面
        model.addAttribute("msg", "输入的用户名或密码有误，请重新输入");
        return "fore/login";
    }

    @RequestMapping("forelogout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:forehome";
    }

    @RequestMapping("foreproduct")
    public String product(Integer pid, Model model) {
        Product product = productService.get(pid);
        List<ProductImage> productSingleImages = productImageService.list(pid, ProductImageService.type_single);
        List<ProductImage> productDetailImages = productImageService.list(pid, ProductImageService.type_detail);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);

        productService.setSaleAndReviewNumber(product);

        List<PropertyValue> propertyValues = propertyValueService.list(pid);
        List<Review> reviews = reviewService.list(pid);

        model.addAttribute("reviews", reviews);
        model.addAttribute("pvs", propertyValues);
        model.addAttribute("p", product);
        return "fore/product";
    }

    @RequestMapping("forecheckLogin")
    @ResponseBody
    public String checkLogin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        return user == null ? "fail" : "success";
    }

    @RequestMapping("foreloginAjax")
    @ResponseBody
    public String loginAjax(HttpSession session, String name, String password) {
        User user = userService.get(name, password);
        if (user == null) {
            return "fail";
        }
        session.setAttribute("user", user);
        return "success";
    }

    @RequestMapping("forecategory")
    public String category(int cid, String sort, Model model) {
        Category c = categoryService.get(cid);
        productService.fill(c);
        productService.setSaleAndReviewNumber(c.getProducts());

        if (null != sort) {
            switch (sort) {
                case "review":
                    Collections.sort(c.getProducts(), new ProductReviewComparator());
                    break;
                case "date":
                    Collections.sort(c.getProducts(), new ProductDateComparator());
                    break;

                case "saleCount":
                    Collections.sort(c.getProducts(), new ProductSaleCountComparator());
                    break;

                case "price":
                    Collections.sort(c.getProducts(), new ProductPriceComparator());
                    break;

                case "all":
                    Collections.sort(c.getProducts(), new ProductAllComparator());
                    break;
            }
        }

        model.addAttribute("c", c);
        return "fore/category";
    }

    @RequestMapping("foresearch")
    public String search(String keyword, Model model) {
        PageHelper.offsetPage(0, 20);
        List<Product> products = productService.search(keyword);
        productService.setSaleAndReviewNumber(products);
        model.addAttribute("ps", products);
        return "fore/searchResult";
    }

    @RequestMapping("forebuyone")
    public String buyone(Integer pid, Integer num, HttpSession session) {
        User user = (User) session.getAttribute("user");

//        查询该用户已有的尚未和订单绑定（即下单）的所有订单项，若有订单项的产品与该立即购买的产品相同，则增加该产品的订单项的数量，否则新建该产品的订单项
        List<OrderItem> orderItems = orderItemService.listByUser(user.getId());
        boolean found = false;
        Integer oiid = null;
        for (OrderItem orderItem :
                orderItems) {
            if (orderItem.getPid().equals(pid)) {
                orderItem.setNumber(num + orderItem.getNumber());
                orderItemService.update(orderItem);
                oiid = orderItem.getId();
                found = true;
                break;
            }
        }

        if (!found) {
            OrderItem orderItem = new OrderItem();
            orderItem.setPid(pid);
            orderItem.setUid(user.getId());
            orderItem.setNumber(num);
            orderItemService.add(orderItem);
            oiid = orderItem.getId();
        }

        return "redirect:forebuy?oiid=" + oiid;
    }

    @RequestMapping("foreaddCart")
    @ResponseBody
    public String addCart(int pid, int num, Model model, HttpSession session) {
        Product p = productService.get(pid);
        User user = (User) session.getAttribute("user");
        boolean found = false;

        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        for (OrderItem oi : ois) {
            if (oi.getProduct().getId().intValue() == p.getId().intValue()) {
                oi.setNumber(oi.getNumber() + num);
                orderItemService.update(oi);
                found = true;
                break;
            }
        }

        if (!found) {
            OrderItem oi = new OrderItem();
            oi.setUid(user.getId());
            oi.setNumber(num);
            oi.setPid(pid);
            orderItemService.add(oi);
        }
        return "success";
    }

    @RequestMapping("forechangeOrderItem")
    @ResponseBody
    public String changeOrderItem(Model model, HttpSession session, int pid, int number) {
        User user = (User) session.getAttribute("user");
        if (null == user)
            return "fail";

        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        for (OrderItem oi : ois) {
            if (oi.getProduct().getId().intValue() == pid) {
                oi.setNumber(number);
                orderItemService.update(oi);
                break;
            }

        }
        return "success";
    }

    @RequestMapping("foredeleteOrderItem")
    @ResponseBody
    public String deleteOrderItem(Model model, HttpSession session, int oiid) {
        User user = (User) session.getAttribute("user");
        if (null == user)
            return "fail";
        orderItemService.delete(oiid);
        return "success";
    }

    @RequestMapping("forecart")
    public String cart(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user.getId());
        model.addAttribute("ois", ois);
        return "fore/cart";
    }

    @RequestMapping("forebuy")
    public String buy(String[] oiid, Model model,HttpSession session) {
        ArrayList<OrderItem> orderItems = new ArrayList<>();
        float total = 0;
        for (String id :
                oiid) {
            OrderItem orderItem = orderItemService.get(Integer.parseInt(id));
            orderItems.add(orderItem);
            total += orderItem.getNumber() * orderItem.getProduct().getPromotePrice();
        }
//        model.addAttribute("ois", orderItems);  在model中添加数据相当于在request域中添加数据
//        跳转到确认订单页jsp后，还要调用forecreateOrder接口，此时request域中没有保存订单详情数据，所以需要保存到session域
        session.setAttribute("ois", orderItems);  
        model.addAttribute("total", total);

        return "fore/buy";
    }

    @RequestMapping("forecreateOrder")
    public String createOrder(Model model, Order order, HttpSession session) {
        User user = (User) session.getAttribute("user");
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUid(user.getId());
        order.setStatus(OrderService.waitPay);
        List<OrderItem> ois = (List<OrderItem>) session.getAttribute("ois");

        float total = orderService.add(order, ois);
        return "redirect:forealipay?oid=" + order.getId() + "&total=" + total;
    }

    @RequestMapping("forepayed")
    public String payed(int oid, float total, Model model) {
        Order order = orderService.get(oid);
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        orderService.update(order);
        model.addAttribute("o", order);
        return "fore/payed";
    }

    @RequestMapping("forebought")
    public String bought( Model model,HttpSession session) {
        User user =(User)  session.getAttribute("user");
        List<Order> os= orderService.list(user.getId(),OrderService.delete);

        orderItemService.fill(os);

        model.addAttribute("os", os);

        return "fore/bought";
    }

    @RequestMapping("foreconfirmPay")
    public String confirmPay( Model model,int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        model.addAttribute("o", o);
        return "fore/confirmPay";
    }

    @RequestMapping("foreorderConfirmed")
    public String orderConfirmed( Model model,int oid) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.waitReview);
        o.setConfirmDate(new Date());
        orderService.update(o);
        return "fore/orderConfirmed";
    }

    @RequestMapping("foredeleteOrder")
    @ResponseBody
    public String deleteOrder( Model model,int oid){
        Order o = orderService.get(oid);
        o.setStatus(OrderService.delete);
        orderService.update(o);
        return "success";
    }

    @RequestMapping("forereview")
    public String review( Model model,int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        Product p = o.getOrderItems().get(0).getProduct();
        List<Review> reviews = reviewService.list(p.getId());
        productService.setSaleAndReviewNumber(p);
        model.addAttribute("p", p);
        model.addAttribute("o", o);
        model.addAttribute("reviews", reviews);
        return "fore/review";
    }

    @RequestMapping("foreoverview")
    public String overview( Model model,HttpSession session,@RequestParam("oid") int oid,@RequestParam("pid") int pid,String content) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.finish);
        orderService.update(o);

        Product p = productService.get(pid);
        content = HtmlUtils.htmlEscape(content);

        User user =(User)  session.getAttribute("user");
        Review review = new Review();
        review.setContent(content);
        review.setPid(pid);
        review.setCreateDate(new Date());
        review.setUid(user.getId());
        reviewService.add(review);

        return "redirect:forereview?oid="+oid+"&showonly=true";
    }

}
