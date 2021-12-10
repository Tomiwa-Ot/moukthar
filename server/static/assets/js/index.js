$(document).ready(function (){
    $.ajax({
       url: '/dashboard',
       type: 'get',
       success: function(data){
           $('.content').html(data);
           $('.content').append(data.htmlresponse);
       }
    });

    $('#nav-dashboard').click(function (){
        $.ajax({
            url: '/dashboard',
            type: 'get',
            success: function(data){
                $('.content').html(data);
                $('.content').append(data.htmlresponse);
            }
        });
    });

    $('#nav-files').click(function (){
        $.ajax({
            url: '/files',
            type: 'get',
            success: function(data){
                $('.content').html(data);
                $('.content').append(data.htmlresponse);
            }
        });
    });
    
    $('#nav-settings').click(function (){
        $.ajax({
            url: '/settings',
            type: 'get',
            success: function(data){
                $('.content').html(data);
                $('.content').append(data.htmlresponse);
            }
        });
    });
});