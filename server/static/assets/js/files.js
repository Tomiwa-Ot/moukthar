$(document).ready(function (){

    var history = []
    var currentPage = -1
    var directory = ["/"]

    $.ajax({
        url: '/get-files',
        type: 'post',
        data: {
            'path' : '/'
        }
        success: function(data) {
            $('.files-class').append(data.htmlresponse);
        }
    })

    $('.file-link').click(function (){
        var path = $(this).text()
        $.ajax({
            url: '/get-files',
            type: 'post',
            data: {
                'path' : `${path}`
            }
            success: function(data) {
                currentPage++
                $('.files-class').html(data.htmlresponse);
            }
        })
    })

    $('#btn-left-arrow').click(function (){
        if (history.length != 0 && currentPage != -1) {
            $.ajax({
                url: '/get-files',
                type: 'post',
                data: {
                    'path' : history[--currentPage]
                }
                success: function(data) {
                    currentPage++
                    $('.files-class').html(data.htmlresponse);
                }
            })
        }
    })

    $('#btn-right-arrow').click(function (){
        if (history.length != 0 && currentPage != history.length) {
            $.ajax({
                url: '/get-files',
                type: 'post',
                data: {
                    'path' : history[++currentPage]
                }
                success: function(data) {
                    $('.files-class').html(data.htmlresponse);
                }
            })
        }
    })
})