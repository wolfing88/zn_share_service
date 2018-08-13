package com.kwon.znshare;

import com.kwon.znshare.repository.ProductRepository;
import com.kwon.znshare.service.BitProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZNShareApplicationTests {

    @Autowired
    ProductRepository productRepository;
    @Autowired
    BitProductService bitProductService;

    @Test
    public void addProduct() {
    }

}
