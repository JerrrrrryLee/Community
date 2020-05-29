package wuya.community.dto;

import lombok.Data;
import wuya.community.model.QuestionDTO;
import java.util.ArrayList;
import java.util.List;

@Data
public class PaginationDTO {
    private List<QuestionDTO> questions;
    private boolean showPrevious;
    private boolean showFirstPage;
    private boolean showNext;
    private boolean showLastPage;
    private Integer page;
    private List<Integer> pages = new ArrayList<>();
    private Integer pagesNumber;


    public void setPagination(Integer totalCount, Integer page, Integer size) {

        this.pagesNumber = (totalCount%size==0)?(totalCount/size):(totalCount/size+1);
        if(page<=0) page=1;
        if(page>pagesNumber) page=pagesNumber;
        this.page=page;
        pages.add(page);
        for (int i = 1; i <= 3; i++) {
            if(page-i>0){
                pages.add(0,page-i);
            }
            if(page+i<=pagesNumber){
                pages.add(page+i);
            }
        }
        this.showPrevious=(page!=1);
        this.showNext=(page!=pagesNumber);
        this.showFirstPage=!pages.contains(1);
        this.showLastPage=!pages.contains(pagesNumber);
    }
}