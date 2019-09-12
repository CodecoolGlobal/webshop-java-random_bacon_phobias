package com.codecool.shop.controller;

import com.codecool.shop.dao.ProductCategoryDao;
import com.codecool.shop.dao.ProductDao;
import com.codecool.shop.dao.implementation.ProductCategoryDaoMem;
import com.codecool.shop.dao.implementation.ProductDaoMem;
import com.codecool.shop.config.TemplateEngineUtil;
import com.codecool.shop.dao.implementation.ShoppingCartDaoMem;
import com.codecool.shop.model.OrderedItem;
import com.codecool.shop.model.Product;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = {"/"})
public class ProductController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ProductDao productDataStore = ProductDaoMem.getInstance();
        ProductCategoryDao productCategoryDataStore = ProductCategoryDaoMem.getInstance();
        ShoppingCartDaoMem shoppingCart = ShoppingCartDaoMem.getInstance();

        TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(req.getServletContext());
        WebContext context = new WebContext(req, resp, req.getServletContext());
        context.setVariable("category", productCategoryDataStore.find(1));
        context.setVariable("products", productDataStore.getBy(productCategoryDataStore.find(1)));
        engine.process("product/index.html", context, resp.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ShoppingCartDaoMem shoppingCart = ShoppingCartDaoMem.getInstance();
        ProductDaoMem productDaoMem = ProductDaoMem.getInstance();
        int productId = Integer.parseInt(req.getParameter("add"));
        Product product = productDaoMem.find(productId);
        OrderedItem orderedItem = new OrderedItem(product.getName(),product.getDefaultPrice(),product.getDefaultCurrency().toString(),product.getDescription(),product.getProductCategory(),product.getSupplier());
        orderedItem.setId(productId);
        addToCart(shoppingCart, orderedItem);
        resp.sendRedirect("product/index.html");

    }

    private void addToCart(ShoppingCartDaoMem shoppingCart, OrderedItem orderedItem) {
        int isNotInTheCart = 0;
        if(shoppingCart.cartItems.isEmpty()){
            shoppingCart.add(orderedItem);
        }
        else{
        for (OrderedItem item:shoppingCart.cartItems) {
            if (item.getId() == orderedItem.getId()) {
                item.increaseQuantity();
                break;
            }
            else{
                    isNotInTheCart++;
                }
        }
        if (isNotInTheCart == shoppingCart.cartItems.size()) {
                shoppingCart.add(orderedItem);
            }
        }
    }
}
