$(function(){
    $("#uploadForm").submit(upload);
});

function upload() {
    $.ajax({
        url: "http://up-z2.qiniup.com",
        method: "post",
        processData: false, //不要把提交的内容转为字符串
        contentType: false, //不让jquery设置上传类型
        data: new FormData($("#uploadForm")[0]),
        success: function(data) {
            if(data && data.code == 200) {
                // 更新头像访问路径
                $.post(
                    CONTEXT_PATH + "/user/header/url",
                    {"fileName":$("input[name='key']").val()},
                    function(data) {
                        data = $.parseJSON(data);
                        if(data.code == 200) {
                            window.location.reload();
                        } else {
                            alert(data.msg);
                        }
                    }
                );
            } else {
                alert("上传失败!");
            }
        }
    });
    //表示事件到此位置不再向下执行表单提交应有的逻辑
    return false;
}


// $(function(){
//     $("form").submit(check_data);
//     $("input").focus(clear_error);
// });
//
// function check_data() {
//     var pwd1 = $("#new-password").val();
//     var pwd2 = $("#confirm-password").val();
//     if(pwd1 != pwd2) {
//         $("#confirm-password").addClass("is-invalid");
//         return false;
//     }
//     return true;
// }
//
// function clear_error() {
//     $(this).removeClass("is-invalid");
// }