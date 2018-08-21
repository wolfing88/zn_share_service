package com.kwon.znshare.controller;

import com.kwon.znshare.entity.MeiNv;
import com.kwon.znshare.service.MeiNvService;
import com.kwon.znshare.vo.CommonVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/meinv")
public class MeiNvController {

    @Autowired
    MeiNvService meiNvService;

    //http://localhost:8080/meinv/list?params=%7b%22page%22%3a1%2c%22pageSize%22%3a20%2c%22type%22%3a%22ALL%22%7d
    @GetMapping(value = "/list")
    public List<MeiNv> getMeiNvList(CommonVo commonVo) {
        return meiNvService.getMeiNvList(commonVo);
    }

}