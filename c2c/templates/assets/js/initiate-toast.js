(function() {
    'use strict';
    
    var toastElList = [].slice.call(document.querySelectorAll('.toast'));

    var toastList = toastElList.map(function (toastEl) {
      return new bootstrap.Toast(toastEl);
    });

    toastList.forEach(toast => toast.show()); 

})();