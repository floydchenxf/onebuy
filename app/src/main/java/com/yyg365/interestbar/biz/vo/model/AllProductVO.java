package com.yyg365.interestbar.biz.vo.model;

import com.yyg365.interestbar.biz.vo.AdvVO;
import com.yyg365.interestbar.biz.vo.product.ProductTypeVO;

import java.util.List;

/**
 * Created by floyd on 16-7-23.
 */
public class AllProductVO {

    public List<ProductTypeVO> typeList; //栏目
    public List<AdvVO> advVOList; //广告
    public List<WinningInfo> theNewList; //商品
}
