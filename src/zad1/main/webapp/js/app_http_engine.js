function AppEngineClass() {

}

const __parametersPrototype = Object.create(__parametersClass.prototype);

__parametersPrototype.__init = function () {
    this.afterSuccess = () => {};
    this.success = () => {};
    this.error = () => {};
}

__parametersPrototype.set = function (param_obj) {
    this.__init();
    for(let key in param_obj) {
        this[key] = param_obj[key];
    }

    let originalError = this.error;
    this.error = (xhr, status, error) => {
        if (xhr === null || xhr === undefined)
            return
    
        if (xhr.status === 401 && xhr.getResponseHeader('AccessToken-Expired') === 'true') {
            AppNavigator.updateRefreshToken({
                url: xhr.getResponseHeader('Location'),
                success: function (response) {
                    AppNavigator.loadingPage();
                },
                error: function (xhr, status, error) {
                    /*if (xhr.status === 401 && xhr.getResponseHeader('RefreshToken-Expired') === 'true') {
                        console.log('Refresh token has expired');
                        AppNavigator.loadingPage();
                    } else { 
                        originalError(xhr, status, error);
                    }*/
                    
                    AppNavigator.loadingPage();
                }
            });
        } else {
            originalError(xhr, status, error);
        }
    }
}

AppEngineClass.prototype.sendMessage = function (parameters) {
    let __parameters = Object.create(__parametersPrototype)
    __parameters.set(parameters);

    if (__parameters.to === __parameters.from ||
        __parameters.to === "" ||
        __parameters.text === "" ||
        __parameters.to === undefined ||
        __parameters.text === undefined ||
        __parameters.to === null ||
        __parameters.text === null ||
        __parameters.to < 0
    ) {
        console.error("Invalid message parameters");
        return;
    }

    let message = {
        to: __parameters.to,
        text: __parameters.text
    };

    $.ajax({
        url: "/Mingle/app/messages",
        type: 'PUT',
        data: JSON.stringify(message),
        contentType: 'application/json',
        success: (response) => __parameters.success(response),
        error: (xhr, status, error) => {
            __parameters.error(xhr, status, error)
        }
      });
}

AppEngineClass.prototype.getMessages = function(parameters) {
    let __parameters = Object.create(__parametersPrototype)
    __parameters.set(parameters);

    if (__parameters.username === "" ||
        __parameters.username === undefined ||
        __parameters.username === null
    ) {
        console.error("Invalid chat id parameter");
        return;
    }

    $.ajax({
        url: "/Mingle/app/messages",
        type: 'POST',
        data: { username: __parameters.username, minutesOffset: new Date().getTimezoneOffset(), timeFrom: __parameters.timeFrom, timeTo: __parameters.timeTo},
        success: (response) => __parameters.success(response),
        error: (xhr, status, error) => __parameters.error(xhr, status, error)
      });
}

AppEngineClass.prototype.getChats = function(parameters) {
    let __parameters = Object.create(__parametersPrototype)
    __parameters.set(parameters);

    $.ajax({
        url: "/Mingle/app/chats",
        type: 'POST',
        data: { minutesOffset: new Date().getTimezoneOffset()},
        success: (response) => __parameters.success(response),
        error: (xhr, status, error) => __parameters.error(xhr, status, error)
      });
}

AppEngineClass.prototype.findUsers = function(parameters) {
    let __parameters = Object.create(__parametersPrototype)
    __parameters.set(parameters);

    if (__parameters.pattern === "" ||
        __parameters.pattern === undefined ||
        __parameters.pattern === null
    ) {
        console.error("Invalid username pattern");
        return;
    }

    $.ajax({
        url: "/Mingle/app/users",
        type: 'POST',
        data: { pattern: __parameters.pattern },
        success: (response) => __parameters.success(response),
        error: (xhr, status, error) => __parameters.error(xhr, status, error)
      });
}

AppEngineClass.prototype.getName = function(parameters) {
    let __parameters = Object.create(__parametersPrototype)
    __parameters.set(parameters);
    $.ajax({
        url: "/Mingle/app/name",
        type: 'POST',
        success: (response) => __parameters.success(response),
        error: (xhr, status, error) => __parameters.error(xhr, status, error)
      });
}

AppEngineClass.prototype.logout = function(parameters) {
    let __parameters = Object.create(__parametersPrototype)
    __parameters.set(parameters);
    $.ajax({
        url: "/Mingle/app/logout",
        type: 'POST',
        success: (response) => __parameters.success(response),
        error: (xhr, status, error) => __parameters.error(xhr, status, error)
      });
}

function AppEngine() {
    return new AppEngineClass()
}