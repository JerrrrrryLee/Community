package wuya.community.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO<T> {
    private List<T> datas;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showLastPage;
    private Integer page;
    private List<Integer> pages = new ArrayList<>();
    private Integer totalPage;

    public void setPagination(Integer totalCount, Integer page, Integer size) {
        totalPage = (totalCount%size==0)?(totalCount/size):(totalCount/size+1);
        if(page < 1) page =1;
        if(page>totalPage) page=totalPage;
        this.page=page;
        pages.add(page);
        for (int i = 1; i <= 3; i++) {
            if(page-i>0){
                pages.add(0,page-i);
            }
            if(page+i<=totalPage){
                pages.add(page+i);
            }
        }
        this.showPrevious=(page!=1);
        this.showNext=(page!=totalPage);
        this.showFirstPage=!pages.contains(1);
        this.showLastPage=!pages.contains(totalPage);
    }
}