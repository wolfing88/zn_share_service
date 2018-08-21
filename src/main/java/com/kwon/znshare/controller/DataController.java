package com.kwon.znshare.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/data")
public class DataController {


//    http://localhost:8080/data/say?id=222
    //    @RequestMapping(value={"/say"},method = RequestMethod.GET)
    @GetMapping(value = "/say")
    public String say(@RequestParam(value = "id",required = false,defaultValue = "0") Integer myId){
        return "id:"+myId;
//        return peoplePerties.getName()+"====="+peoplePerties.getAge();
//        return "index";
    }
}