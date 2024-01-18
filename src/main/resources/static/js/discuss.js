$(function (){
    // $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
});

function like(btn, entityType, entityId, entityUserId, postId){
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType": entityType, "entityId":entityId, "entityUserId":entityUserId, "postId":postId},
        function (data){
            data = $.parseJSON(data);
            if(data.code == 0){
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus == 1? '已赞' : "赞");
            }else{
                alert(data.msg);
            }
        }
    );
}

//置顶
// function setTop(){
//     $.post(
//         CONTEXT_PATH + "/discuss/top",
//         {"id":$("#postId").val()},
//         function (data){
//             data = $.parseJSON(data);
//             if(data.code == 0){
//                 $("#topBtn").attr("disabled", "disabled");
//             }else{
//                 alert(data.msg);
//             }
//         }
//     );
// }


function setTop(button, postId, postType) {
    var newType = postType == 1? 0:1;

    // 发送 AJAX 请求
    $.post(
        CONTEXT_PATH + "/discuss/top",
        {"id": postId, "type": newType},  // 传递帖子ID和当前状态
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                // 更新按钮文本
                window.location.reload();
                // $(button).children("b").text(data.Type == 1? '取消置顶' : "置顶");
            } else {
                alert(data.msg);
            }
        }
    );
}



//加精
function setWonderful(){
    $.post(
        CONTEXT_PATH + "/discuss/wonderful",
        {"id":$("#postId").val()},
        function (data){
            data = $.parseJSON(data);
            if(data.code == 0){
                $("#wonderfulBtn").attr("disabled", "disabled");
            }else{
                alert(data.msg);
            }
        }
    );
}

//删除
function setDelete(){
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"id":$("#postId").val()},
        function (data){
            data = $.parseJSON(data);
            if(data.code == 0){
                location.href = CONTEXT_PATH + "/index";
            }else{
                alert(data.msg);
            }
        }
    );
}