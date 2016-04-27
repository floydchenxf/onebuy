package com.floyd.onebuy.biz.vo.json;

import com.floyd.onebuy.biz.vo.product.ProductTypeVO;

import java.util.List;

/**
 * Created by floyd on 16-4-26.
 */
public class IndexVO {

    public List<IndexAdvVO> advertisList; //图片广告
    public List<WordNewsVO> wordList;//文字广告
    public List<ProductTypeVO> typeList; //类型导航
    public List<ProductLssueItemVO> theNewList; //期数商品
    public String Image;//图片广告
}
