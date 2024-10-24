if (typeof __parametersClass === 'undefined') {
    function __parametersClass() {
        this.__init();
    }
}
if (typeof AppNavigatorClass === 'undefined') {
    function AppNavigatorClass() {
        this.__parameters = new __parametersClass();
    }
}

__parametersClass.prototype.__init = function() {
    this.url = "";
    this.formData = "";
    this.success = () => {};
    this.error = () => {};
}

__parametersClass.prototype.set = function (param_obj) {
    this.__init();
    for(let key in param_obj) {
        this[key] = param_obj[key];
    }
}

AppNavigatorClass.prototype.getCurrentUrl = function() {
    let url = window.location.href;
    url = url.endsWith('/') ? url.substring(0, url.length - 1) : url;
    return url;
}

AppNavigatorClass.prototype.openApp = function(parameters){
    this.__parameters.set(parameters);
    $.ajax({
      url: "/Mingle/app",
      type: 'POST',
      success: (response) => this.__parameters.success(response),
      error: (xhr, status, error) => this.__parameters.error(xhr, status, error)
    });
}

AppNavigatorClass.prototype.login = function (parameters){
    this.__parameters.set(parameters);
    $.ajax({
        type: "POST",
        url: "/Mingle/login",
        data: this.__parameters.formData,
        success: (response) => this.__parameters.success(response),
        error: (xhr, status, error) => this.__parameters.error(xhr, status, error)
    });
}

AppNavigatorClass.prototype.registerPage = function (){
    window.location.href = "/Mingle/register";
}
AppNavigatorClass.prototype.register = function (parameters){
    this.__parameters.set(parameters);
    $.ajax({
        type: "POST",
        url: "/Mingle/register",
        data: this.__parameters.formData,
        success: (response) => this.__parameters.success(response),
        error: (xhr, status, error) => this.__parameters.error(xhr, status, error)
    });
}

AppNavigatorClass.prototype.loadingPage = function (){
    window.location.href = "/Mingle";
}

AppNavigatorClass.prototype.updateRefreshToken = function(parameters) {
    this.__parameters.set(parameters);
    $.ajax({
      url: this.__parameters.url,
      type: 'POST',
      success: (response) => this.__parameters.success(response),
      error: (xhr, status, error) =>  this.__parameters.error(xhr, status, error)
    });
}

if (typeof AppNavigator === 'undefined') {
    var AppNavigator = new AppNavigatorClass();
}