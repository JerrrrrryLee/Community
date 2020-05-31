// 提交回复
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2Target(questionId,1,content);
}

function comment2Target(targetId,type,content) {

    if(!content){
        alert("不能回复空内容！");
        return;
    }
    $.ajax({
        type:"POST",
        url:"/comment",
        contentType:"application/json",
        data:JSON.stringify({
            "parentId":targetId,
            "content":content,
            "type":type
        }),
        success:function(response){
            if(response.code==200){
                window.location.reload();
            }else {
                if(response.code==2003){
                    var isAccepted = confirm(response.message);
                    if(isAccepted){
                        window.open("https://github.com/login/oauth/authorize?client_id=076bb4a4aad4e1247bd5&redirect_uri=http://localhost:8887/callback&scope=user&state=1");
                        window.localStorage.setItem("closable", true);
                    }


                }else {
                    alert(response.message());
                }
            }
            console.log(response)
        },
        dataType:"json"
    });

}
function comment(e) {
    var commentId = e.getAttribute("data-id");
    var content = $("#input-"+commentId).val();
    comment2Target(commentId,2,content);
}

// 展开二级评论
function collapseComments(e){
    var id = e.getAttribute("data-id");
    var comments = $("#comment-"+id);
    var collapse = e.getAttribute("data-collapse");
    if(collapse){
        // 折叠
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("icon-active");
    }else{
        var subComments = $("#comment-"+id);
        if(subComments.children().length!=1){
            comments.addClass("in");
            e.setAttribute("data-collapse","in");
            e.classList.add("icon-active");
        }else{
            // 展开
            $.getJSON("/comment/"+id,function (data) {
                $.each(data.data.reverse(),function (index,comment) {
                    var mediaBodyElement=$("<div/>",{
                        "class":"media-body"
                    }).append($("<h5/>",{
                        "class":"media-heading"
                    })
                        .append($("<span/>",{
                        "class":"mt-0 mb-1",
                        "text":comment.user.name
                    })
                        )).append($("<div/>",{
                        "text":comment.content
                    }));
                    var imgElement=$("<img/>",{
                        "class":"media-object img-circle",
                        "src":comment.user.avatarUrl
                    });
                    var mediaLeftElement=$("<div/>",{
                        "class":"media-left media-middle"
                    }).append(imgElement);

                    var mediaElement=$("<div/>",{
                        "class":"media"
                    }).append(mediaLeftElement)
                        .append(mediaBodyElement);
                    var innerA=$("<div/>",{
                        "class":"col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12"
                    }).append(mediaElement);
                    var display =$("<div/>",{}).append(innerA);
                    var operator=$("<div/>",{
                        "class":"col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12",
                        "style":"margin-top:5px"
                    }).append($("<span/>",{
                        "class":"pull-right text-index",
                        "html":moment(comment.gmtCreate).format('YYYY-MM-DD')
                    }));

                    var hr=$("<hr/>",{
                        "class":"col-12 col-sm-12 col-md-12 col-lg-12 col-xl-12"
                    });

                    subComments.prepend(hr).prepend(operator).prepend(display);
                });
                comments.addClass("in");
                e.setAttribute("data-collapse","in");
                e.classList.add("icon-active");
            });
        }
    }
}

function selectTag(e) {
    var value = e.getAttribute("data-tag");
    var previous = $("#tag").val();
    if(previous.indexOf(value) == -1){
        if(previous){
            $("#tag").val(previous+','+value)
        }else{
            $("#tag").val(value)
        }
    }
}

function popupTag() {
    $("#tag-list").show();
}