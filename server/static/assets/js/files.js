$(document).ready(function (){
    // on a click get text post to files
    // provide forward and backward navigation linked list??

    $('#btn-left-arrow').click(function (){
        $.ajax({
            url: '/get-files',
            type: 'post',
            data: {}
            success: function(data){
                
            }
        })
    })

    $('#btn-right-arrow').click(function (){
        $.ajax({
            url: '/get-files',
            type: 'post',
            data: {}
            success: function(data){
                
            }
        });
    });
})