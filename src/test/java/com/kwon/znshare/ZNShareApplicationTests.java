package com.kwon.znshare;

import com.kwon.znshare.service.MeiNvService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ZNShareApplicationTests {

    @Autowired
    MeiNvService meiNvService;

    @Test
    public void addProduct() {
        meiNvService.getMeiNvImg();
    }

}
