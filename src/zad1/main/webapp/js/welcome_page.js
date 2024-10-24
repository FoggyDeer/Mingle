let currUrl = window.location.href;
currUrl = currUrl.replace(window.location.origin, '')
if(currUrl == "/" || currUrl == ""){
  window.location.href = window.location.origin + "/Mingle";
}

document.addEventListener("DOMContentLoaded", function() {
    const element = document.getElementById("logo");

    element.classList.add("slide-in");

    element.addEventListener("animationend", function() {
        element.classList.remove("slide-in");
        element.classList.add("waving");

        element.addEventListener("animationiteration", AppNavigator.openApp({
          success: function (response) {

            const html = document.createElement('html');
            html.innerHTML = response;
            document.documentElement.replaceWith(html);
            executeScripts();
          },
          error: function(xhr, status, error) {
              if (xhr.status === 401) {
                if(xhr.getResponseHeader('AccessToken-Expired') === 'true'){
                    console.log('Access token has expired');

                    AppNavigator.updateRefreshToken({
                        url: xhr.getResponseHeader('Location'),
                        success: function(response) {
                          AppNavigator.loadingPage();
                        },
                        error: function(xhr, status, error) {
                          if (xhr.status === 401 && xhr.getResponseHeader('RefreshToken-Expired') === 'true') {
                              console.log('Refresh token has expired');
                          }
                          AppNavigator.loadingPage();
                        }
                    });

                } else {
                  console.log(xhr)
                  console.log('You must login');
                  console.log(xhr.getResponseHeader('Location'));
                  window.location.href = xhr.getResponseHeader('Location');
                }
              } 
            }
        }), { once: true });
    }, { once: true });
});


function executeScripts() {
  const newScripts = document.querySelectorAll('script');
        
  newScripts.forEach(oldScript => {
      const newScript = document.createElement('script');
      if (oldScript.src) {
          newScript.src = oldScript.src;
      } else {
          newScript.textContent = oldScript.innerHTML;
      }
      document.body.appendChild(newScript);
      oldScript.remove();
  });
}
