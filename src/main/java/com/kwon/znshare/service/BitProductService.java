package com.kwon.znshare.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BitProductService {

    protected static final Logger logger = LoggerFactory.getLogger(BitProductService.class);

    @Async
    @Transactional
    public void saveBitProduct() {
    }
}
