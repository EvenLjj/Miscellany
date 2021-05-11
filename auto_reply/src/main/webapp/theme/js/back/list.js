function deleteMessages(){
    $("#mainForm").attr("action","/auto_reply/deleteMessages.action");
    $("#mainForm").submit();
}
/**
 * 修改当前页码，调用后台重新查询
 */
function changeCurrentPage(currentPage) {
    var a=$("#currentPage").val(currentPage);
    $("#mainForm").submit();
}