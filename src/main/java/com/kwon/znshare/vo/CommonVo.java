package com.kwon.znshare.vo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class CommonVo {
    private String params;
    private String type;
    private int page;
    private int pageSize;

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        JSONObject obj = JSONObject.parseObject(params);
        this.page = obj.getIntValue("page");
        this.pageSize = obj.getIntValue("pageSize");
        this.type = obj.getString("type");
        this.params = params;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
