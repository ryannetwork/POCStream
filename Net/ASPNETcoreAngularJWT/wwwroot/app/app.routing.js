"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var router_1 = require("@angular/router");
var home_component_1 = require("./home/home.component");
var about_component_1 = require("./about/about.component");
var login_component_1 = require("./login/login.component");
var secret_component_1 = require("./secret/secret.component");
var auth_service_1 = require("./shared/auth.service");
var appRoutes = [
    { path: "", redirectTo: "login", pathMatch: "full" },
    { path: 'about', component: about_component_1.AboutComponent },
    { path: 'login', component: login_component_1.LoginComponent },
    { path: 'home', component: home_component_1.HomeComponent },
    { path: 'secret', component: secret_component_1.SecretComponent, canActivate: [auth_service_1.AuthService] }
];
exports.routing = router_1.RouterModule.forRoot(appRoutes);
//# sourceMappingURL=app.routing.js.map