<%@ page import="pjwstk.tpo_6.mingle.Utils.ErrorsManager" %>
<%@ page isErrorPage="true" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Mingle</title>
        <link rel="stylesheet" type="text/css" href="/css/error_page.css">
    </head>
    <body>
        <img id="logo" src="/assets/logo/Logo_Mini.svg" alt="logo">
        <main>
            <div class="container">
                <p><%=response.getStatus()%> - <%=ErrorsManager.getMessageByCode(response.getStatus())%> :(</p>
                <a href="/Mingle">
                    <div id="home">Home</div>
                </a>
            </div>
            
        </main>
    </body>
</html>