$(document).ready(function (){
    $("a").click(function(){
        console.log(this.id)
        if(this.id.startsWith("test-connection")){
            var id = this.id.replace("test-connection-", "")
            var socketid = document.getElementsByClassName(`socket-id-${id}`)
            var deviceid = document.getElementsByClassName(`device-id-${id}`)
            $.ajax({
                url: '/log',
                type: 'post',
                data: {
                'data' : `Emitting ping event to ${id} ...`,
                'highlight' : 'light'
                },
                success: function(data){
                    $('.log-class').append(data.htmlresponse)
                    $.ajax({
                        url: '/pingclient',
                        type: 'post',
                        data: {
                        'socketid' : socketid,
                        'deviceid' : devieid
                        },
                        success: function(data){
                            $.ajax({
                                url: '/log',
                                type: 'post',
                                data: {
                                'data' : 'Device pinged. Waiting for response ...',
                                'highlight' : 'light'
                                },
                                success: function(data){
                                    $('.log-class').append(data.htmlresponse);
                                    // perform check to see if device does not respond. possibly a 15 seconds timer to execute
                                }
                            });
                        }
                    });
                }
            });
        }

        if(this.id.startsWith("remove")){
            var id = this.id.replace("remove-", "")
            $.ajax({
                url: '/log',
                type: 'post',
                data: {
                'data' : `Deleting ${id} from database ...`,
                'highlight' : 'light'
                },
                success: function(data){
                    $('.log-class').append(data.htmlresponse);
                    $.ajax({
                        url: '/deleteclient',
                        type: 'post',
                        data: {
                            'deviceid' : id
                        },
                        success: function(data){
                            console.log(data)
                            // check response/status code to be sure record was deleted
                            var client = document.getElementsByClassName(`client-${id}`)
                            client.remove()
                            $.ajax({
                                url: '/log',
                                type: 'post',
                                data: {
                                'data' : `Successfully deleted ${id} from database`,
                                'highlight' : 'success'
                                },
                                success: function(data){
                                    $('.log-class').append(data.htmlresponse);
                                }
                            });
                        }
                    });
                }
            });
        }
    })
})