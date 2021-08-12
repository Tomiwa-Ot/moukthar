// Initiate summernote wysiwyg editor
(function() {
    'use strict';

    $('#summernote').summernote({
        dialogsInBody: true,
        minHeight: 300,
        toolbar: [
            ['style', ['bold', 'italic', 'underline', 'clear']],
            ['font', ['strikethrough']],
            ['para', ['paragraph']],
            ['list', ['ul']],
            ['numberlist', ['ol']]
        ]
    });
})();