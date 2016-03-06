<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="en">
<head>
    <meta http-equiv="content-type" content="text/html" charset="UTF-8">
    <title>Order Store</title>
    <link href="<c:url value="/resources/css/style.css" />" rel="stylesheet"  type="text/css" />
    <link href="<c:url value="/resources/css/jquery.jsonview.css" />" rel="stylesheet"  type="text/css" />
    <link href="<c:url value="/resources/css/button.css" />" rel="stylesheet"  type="text/css" />
    <script type="text/javascript" src="<c:url value="/resources/js/jquery-2.1.4.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/ajaxfileupload.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/jquery.jsonview.js" />"></script>
    <script type="text/javascript" src="<c:url value="/resources/js/index.js" />"></script>
</head>
<body>
<div id="page-wrapper">
    <h4 class="h4">Action</h4>
    <table class="action-table">
        <tr>
            <td>
                <div class="radio">
                    <label for="createOption"><input type="radio" id="createOption" name="options" value="create">
                        Create Order</label>
                    <br>
                    <label for="lookupOption"><input type="radio" id="lookupOption" name="options" value="lookup">
                        Lookup Order</label>
                </div>
            </td>
        </tr>
    </table>
    <h4 class="h4">Parameters</h4>

    <table class="param-table" id="mainTable" style="display: none;">
        <tr class="parameter" id="uploadParam">
            <td class="td-align-top">
                <h4 class="margin-top indent" id="xml-to-upload">XML to upload:</h4>
            </td>
            <td class="td-align-top">
                <form id="uploadForm">
                    <label for="fileInput" class="btn green">
                        Choose the XML
                    </label>
                    <input type="file" name="file" id="fileInput" class="margin-top" accept=".xml"
                           style="display: none;">
                </form>
            </td>
        </tr>

        <tr class="parameter" id="lookupParam">
            <td class="td-align-top">
                <h4 class="margin-top indent">Order ID:</h4>
            </td>
            <td class="td-align-top">
                <form id="lookupForm">
                    <label for="lookupText">
                    </label>
                    <input type="text" name="lookup" id="lookupText" class="margin-top" required>
                    <br>
                    <label for="lookupSubmit" class="btn green">
                        Lookup
                    </label>
                    <input type="submit" value="Lookup" name="lookup" id="lookupSubmit" style="display: none;">
                </form>
            </td>
        </tr>
    </table>

    <table class="param-table" id="init">
        <tr>
            <td>
                <div class="init-table">
                    Please, choose the option above.
                </div>
            </td>
        </tr>
    </table>
</div>

<div class="messages">
    <div class="success">
        <div id="uploadResult" style="display: none;"></div>
        <div id="lookupID" style="display: none"></div>
        <div id="lookupResult" style="display: none;"></div>
    </div>

    <div class="errors">
        <div id="errorUploadMsg" style="display: none;"><p>An error
            occurred while the file being uploaded!</p></div>
        <div id="errorLookupMsg" style="display: none;">
            <p>Error! There's no order with such ID!</p>
        </div>
    </div>
</div>
</body>
</html>