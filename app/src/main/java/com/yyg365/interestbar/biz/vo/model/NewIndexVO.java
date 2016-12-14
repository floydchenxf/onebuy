package com.yyg365.interestbar.biz.vo.model;

import com.yyg365.interestbar.biz.vo.AdvVO;
import com.yyg365.interestbar.biz.vo.json.WordNewsVO;
import com.yyg365.interestbar.biz.vo.product.ProductTypeVO;

import java.util.List;

/**
 * Created by floyd on 16-4-26.
 */
public class NewIndexVO {

    public List<AdvVO> advertisList;
    public List<WordNewsVO> wordList;
    public List<ProductTypeVO> typeList;
    public List<WinningInfo> theNewList;
    public String newsImageUrl;
}
