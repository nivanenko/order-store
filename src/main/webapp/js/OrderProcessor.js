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
  var lookupID = '#lookupID';

  var errorUploadMsg = '#errorUploadMsg';
  var errorLookupMsg = '#errorLookupMsg';

  $('input[name="options"]').change(function () {
    var value = $(this).val();
    $(mainTable).show();

    switch (value) {
      case "create":
        $(lookupID).hide();
        $(lookupResult).hide();
        $(uploadResult).hide();
        $(errorUploadMsg).hide();
        $(errorLookupMsg).hide();
        $(lookupParam).hide();
        $(init).hide();
        $(uploadParam).show();
        break;
      case "lookup":
        $(lookupID).hide();
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
    'action': '/asyncUpload',
    'onComplete': function (response) {
      if (isNaN(response) || response === 0) {
        $(uploadResult).hide();
        $(fileInput).text('Choose the XML');
        if (!(response === 0)) {
          $(errorUploadMsg).append('<p>Error: ' + response + '</p>');
        }
        $(errorUploadMsg).show();
      } else {
        $(errorUploadMsg).hide();
        $(uploadResult).html('' +
          '<p>Order\'s been successfully saved. Order ID: <span style=\'color: green; font-weight: bold\'>' + response + '</span></p>').show();
        $(fileInput).text('Choose the XML');
      }
    },
    'onStart': function () {
      $(fileInput).text('Processing the file...');
      $(errorUploadMsg).hide();
      $(uploadResult).hide();
    },
    onCancel: function () {
      $(uploadResult).hide();
      $(fileInput).text('Choose the XML');
      $(errorUploadMsg).show();
    }
  });

  $(lookupForm).submit(function (e) {
    e.preventDefault();
    $(lookupSubmit).text('Looking up...');
    var data = $(lookupText).val();

    $.ajax({
      type: 'POST',
      url: '/lookup',
      data: {value: data},
      encode: true,
      success: function (response) {
        if (response === 'error') {
          $(lookupID).hide();
          $(lookupResult).hide();

          $(errorLookupMsg).show();
          $(lookupSubmit).text('Lookup');
        } else {
          $(errorLookupMsg).hide();

          $(lookupID).html('<p>Order for ID: <span style=\'color: green; font-weight: bold\'>' + data
            + '</span></p>').show();
          var json = JSON.stringify(response);
          $(lookupResult).JSONView(json).show();
          $(lookupSubmit).text('Lookup');
        }
      },
      error: function () {
        $(lookupID).hide();
        $(errorLookupMsg).show();
        $(lookupSubmit).text('Lookup');
      }
    });
  });
});