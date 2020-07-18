package com.example.demo.service;


import com.example.demo.dto.ProductDTO;
import com.example.demo.dto.ShoppingCartHistory;
import com.example.demo.dto.ShoppingCartInfo;
import com.example.demo.model.Bill;
import com.example.demo.model.Laptop;
import com.example.demo.model.Product;
import com.example.demo.model.ShoppingCart;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.note.StaticVariables.*;

@Service
public final class ProductDetailsService {

    private final ProductRepository productRepository;
    private final ProductTypeRepository productTypeRepository;
    private final TypeRepository typeRepository;
    private final ModelMapper modelMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final BillRepository billRepository;
    private final UserRepository userRepository;
    private final LapRepository lapRepository;

    public ProductDetailsService(ProductRepository productRepository, ProductTypeRepository productTypeRepository, TypeRepository typeRepository, ModelMapper modelMapper, ShoppingCartRepository shoppingCartRepository, BillRepository billRepository, UserRepository userRepository, LapRepository lapRepository) {
        this.productRepository = productRepository;
        this.productTypeRepository = productTypeRepository;
        this.typeRepository = typeRepository;
        this.modelMapper = modelMapper;
        this.shoppingCartRepository = shoppingCartRepository;
        this.billRepository = billRepository;
        this.userRepository = userRepository;
        this.lapRepository = lapRepository;
    }


    boolean addProduct(String productName, double productCost) {
        if (productRepository.findByProductName(productName) != null) {
            return false;
        }
        Product product = new Product();
        product.setProductName(productName);
        product.setProductCost(productCost);
        productRepository.save(product);
        return true;
    }


    private String getProductType(int productId) {
        int typeId = productTypeRepository.findByProductId(productId).getTypeId();
        return typeRepository.findByTypeId(typeId).getTypeName();
    }

    ProductDTO findOneProduct(int productId) {
        if (!productRepository.findById(productId).isPresent()) {
            return null;
        }
        Product product = productRepository.findByProductId(productId);
        return modelMapper.mapProduct(product, getProductType(productId));
    }

    List<Product> findAllProduct() {
        return productRepository.findAll();
    }

    boolean changeProductName(String productCurrentName, String productName) {
        if (isProductExist(productCurrentName)) {
            Product product = productRepository.findByProductName(productCurrentName);
            product.setProductName(productName);
            productRepository.save(product);
            return true;
        }
        return false;
    }

    boolean changeProductCost(String productName, double productCost) {
        if (isProductExist(productName)) {
            Product product = productRepository.findByProductName(productName);
            product.setProductCost(productCost);
            productRepository.save(product);
            return true;
        }
        return false;
    }

    private boolean isProductExist(String productName) {
        return productRepository.findByProductName(productName) != null;
    }

    boolean addShoppingCart(String listProduct){
        double result = 0;
        if(checkBillInProcess()){
            int userId = userRepository.findByUserName(CURRENT_USER).getUserId();
            String status = "process";
            int billId = billRepository.findByUserIdAndStatus(userId,status).getBillId();
            System.out.println(1);
            if(!checkProductInShoppingCart(listProduct,billId)){
                return false;
            }
            String[] list = listProduct.split(",");
            for (String product:list
                 ) {
                if(!isProductExist(product))
                    return false;
                addProductInShoppingCart(product,billId);
                result += productRepository.findByProductName(product).getProductCost();
            }
            Bill bill1 = billRepository.findByBillId(billId);
            bill1.setTotalCost(result + bill1.getTotalCost());
            billRepository.save(bill1);
            return true;
        }

        Bill bill = new Bill();
        bill.setStatus(PROCESS);
        bill.setUserId(userRepository.findByUserName(CURRENT_USER).getUserId());
        billRepository.save(bill);

        int billId = bill.getBillId();
        String[] list = listProduct.split(",");
        for(String product : list){
            System.out.println(product);
            if(!isProductExist(product))
                return false;
            addProductInShoppingCart(product,billId);
            result += productRepository.findByProductName(product).getProductCost();
        }

        Bill bill1 = billRepository.findByBillId(billId);
        bill1.setTotalCost(result);
        billRepository.save(bill1);
        return true;
    }

    private boolean checkProductInShoppingCart(String listProduct, int billId){
        List<ShoppingCart> cartList;
  //      List<String> list = Arrays.asList(listProduct.split(","));
        String[] list = listProduct.split(",");
        cartList = shoppingCartRepository.findShoppingCartsByBillId(billId);

        for (ShoppingCart cart:
             cartList) {
           String productName = productRepository.findByProductId(cart.getProductId()).getProductName();

            for (String product:list
                 ) {
                System.out.println(product + " " + productName);
                System.out.println(productName.equals(product));
               if(productName.equals(product))
               {
                   return false;
               }
            }
        }
       return true;
    }


    private boolean checkBillInProcess(){
        int userId = userRepository.findByUserName(CURRENT_USER).getUserId();
        String status = "process";

        return billRepository.findByUserIdAndStatus(userId,status)!= null;
    }

    private void addProductInShoppingCart(String product, int billId){
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setBillId(billId);
        shoppingCart.setProductId(productRepository.findByProductName(product).getProductId());
        shoppingCartRepository.save(shoppingCart);
    }

    boolean pay(String code, int billId){
        if(code.equals(P_CODE)){
            Bill bill = billRepository.findByBillId(billId);
            bill.setStatus(PAID);
            billRepository.save(bill);
            return true;
        }
        return false;
    }

     ShoppingCartHistory getShoppingCartHistory(){
        int userId = userRepository.findByUserName(CURRENT_USER).getUserId();
        List<Bill> billList = billRepository.findBillsByUserId(userId);
        ShoppingCartHistory shoppingCartHistory = new ShoppingCartHistory();
        List<ShoppingCartInfo> shoppingCartInfoList = new ArrayList<>();
        for (Bill bill:billList
             ) {
            int billId = bill.getBillId();
            ShoppingCartInfo shoppingCartInfo = new ShoppingCartInfo();
            List<ShoppingCart> shoppingCartList = shoppingCartRepository.findShoppingCartsByBillId(billId);
            List<ProductDTO> productDTOList = new ArrayList<>();
            for (ShoppingCart shoppingCart:shoppingCartList
                 ) {
                Product product = productRepository.findByProductId(shoppingCart.getProductId());
                productDTOList.add(modelMapper.mapProduct(product,getProductType(product.getProductId())));
            }
            shoppingCartInfo.setBillId(billId);
            shoppingCartInfo.setTotalCost(bill.getTotalCost());
            shoppingCartInfo.setList(productDTOList);
            shoppingCartInfo.setStatus(bill.getStatus());
            shoppingCartInfoList.add(shoppingCartInfo);
        }
        shoppingCartHistory.setList(shoppingCartInfoList);
        shoppingCartHistory.setUserName(CURRENT_USER);
        return shoppingCartHistory;
    }


    public List<Laptop> findByBrand(String brand){
        return lapRepository.findAllByBrand(brand);
    }

    public List<Laptop> findByCost(int type){
        List<Laptop> laptops = new ArrayList<>();
        switch (type){
            case 1:
                for (Laptop laptop:lapRepository.findAll()
                ) {
                    if(laptop.getCost()>=429 && laptop.getCost()<=643){
                        laptops.add(laptop);
                    }
                }
                break;
            case 2:
                for (Laptop laptop:lapRepository.findAll()
                ) {
                    if(laptop.getCost()>643 && laptop.getCost()<=858){
                        laptops.add(laptop);
                    }
                }
                break;
        }
        return laptops;
    }

    public List<Laptop> findByBrandAndCost(int type, String brand){
        List<Laptop> laptops = findByBrand(brand);
        List<Laptop> laptops1 = new ArrayList<>();
        switch (type){
            case 1:
                for (Laptop laptop:laptops
                ) {
                    if(laptop.getCost()>=429 && laptop.getCost()<=643){
                        laptops1.add(laptop);
                    }
                }
                break;
            case 2:
                for (Laptop laptop:laptops
                ) {
                    if(laptop.getCost()>643 && laptop.getCost()<=858){
                        laptops1.add(laptop);
                    }
                }
                break;
        }
        return laptops1;
    }

    public List<Laptop> findBy(String text){
        if(text.substring(0,1).equals("d")){
            return findByBrand("dell");
        }
        else{
            return  findByBrand("asus");
        }
    }
}
