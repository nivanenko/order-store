/*global $:false */
$(document).ready(function () {
  "use strict";

  var init = '#init';
  var parameter = '.parameter';
  var mainTable = '#mainTable';
  var fileInput = 'label[for=\'fileInput\']';

  var uploadParam = '#uploadParam';
  var uploadForm = '#uploadForm';
  var uploadResult = '#uploadResult';

  var lookupParam = '#lookupParam';
  var lookupForm = '#lookupForm';
  var lookupText = '#lookupText';
  var lookupResult = '#lookupResult';
  var lookupSubmit = 'label[for=\'lookupSubmit\']';

  var errorUploadMsg = '#errorUploadMsg';
  var errorLookupMsg = '#errorLookupMsg';

  $('input[name="options"]').change(function () {
    var value = $(this).val();
    $(mainTable).show();

    switch (value) {
      case "create":
        $(lookupResult).hide();
        $(uploadResult).hide();
        $(errorUploadMsg).hide();
        $(errorLookupMsg).hide();
        $(lookupParam).hide();
        $(init).hide();
        $(uploadParam).show();
        break;
      case "lookup":
        $(lookupResult).hide();
        $(uploadResult).hide();
        $(errorUploadMsg).hide();
        $(errorLookupMsg).hide();
        $(uploadParam).hide();
        $(init).hide();
        $(lookupParam).show();
        break;
    }
  });

  // Using AjaxFileUpload lib
  $('input[type="file"]').ajaxfileupload({
    'action': '/FileUploadServlet',
    'onComplete': function (response) {
      $(uploadResult).append('' +
        '<p>Order\'s been successfully saved. Order ID: <span style=\'color: green; font-weight: bold\'>' + response + "</span></p>").show();
      $(fileInput).text('Choose the XML');
    },
    'onStart': function () {
      $(fileInput).text('Processing the file...');
      $(errorUploadMsg).hide();
      $(uploadResult).hide();
    },
    onCancel: function () {
      $(fileInput).text('Choose the XML');
      $(errorUploadMsg).show();
      $(uploadResult).hide();
    }
  });

  $(lookupForm).submit(function (e) {
    e.preventDefault();
    $(lookupSubmit).text('Looking up...');
    var data = $(lookupText).val();

    $.ajax({
      type: 'POST',
      url: '/LookupServlet',
      data: {value: data},
      encode: true,
        success: function (response) {
        if (response === 'error') {
          $(errorLookupMsg).show();
          $(lookupResult).hide();
          $(lookupSubmit).text('Lookup');
        } else {
          var json = JSON.stringify(response);
          $(lookupResult).JSONView(json).show();
          $(errorLookupMsg).hide();
          $(lookupSubmit).text('Lookup');
        }
      },
      error: function () {
        $(errorLookupMsg).show();
        $(lookupSubmit).text('Lookup');
      }
    });
  });
});