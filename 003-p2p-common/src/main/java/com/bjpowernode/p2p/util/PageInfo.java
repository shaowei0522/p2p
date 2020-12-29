package com.bjpowernode.p2p.util;

import java.io.Serializable;

/**
 * @ProjectName: p2p
 * @Package: com.bjpowernode.p2p.model
 * @Description: 借款分页的相关信息,
 * pageSize,pageContent,currentPage,
 * totalPage= pageContent % pageSize == 0 ? pageContent/pageSize : pageContent /pageSize +1
 *
 *
 *
 * 0-8    start :pageSize * (currentPage- 1)  pageSize : pageSize
 * 9-17
 * 18-25
 * @Author: 王少伟
 * @CreateDate: 2020/12/16 09:30
 * @Version: 1.0
 * <p>
 * Copyright: Copyright (c) 2020
 */
public class PageInfo implements Serializable {

//    每页的信息个数
    private Integer pageSize = 9;
//    当前的页码
    private Integer currentPage;
//    总页数，总页数可以通过所有的信息个数与每页的个数计算出来，所以不需要设置set方法
    private Integer pages;
//    所有的信息个数
    private Integer pageContent;


    public PageInfo(){

    }
    public PageInfo(Integer pageContent){
        this.pageContent = pageContent;

    }
    @Override
    public String toString() {
        return "PageInfo{" +
                "pageSize=" + pageSize +
                ", currentPage=" + currentPage +
                ", pages=" + pages +
                ", pageContent=" + pageContent +
                '}';
    }

    public Integer getPageContent() {
        return pageContent;
    }

    public void setPageContent(Integer pageContent) {
        this.pageContent = pageContent;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPages() {
        return pageContent % pageSize == 0 ? pageContent / pageSize : pageContent/pageSize + 1;
    }

}
