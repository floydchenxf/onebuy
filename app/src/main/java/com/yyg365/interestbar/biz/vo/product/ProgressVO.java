package com.yyg365.interestbar.biz.vo.product;

/**
 * Created by floyd on 16-4-17.
 */
public class ProgressVO {
    public int TotalCount;
    public int JonidedCount;

    public int getPrecent() {
        return this.JonidedCount * 100 / this.TotalCount;
    }
}
