"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
var core_1 = require("@angular/core");
var http_1 = require("@angular/common/http");
var router_1 = require("@angular/router");
var of_1 = require("rxjs/observable/of");
var operators_1 = require("rxjs/operators");
var AuthService = /** @class */ (function () {
    function AuthService(http, router) {
        this.http = http;
        this.router = router;
        this.tokeyKey = "token";
    }
    AuthService.prototype.canActivate = function () {
        if (this.checkLogin()) {
            return true;
        }
        else {
            this.router.navigate(['login']);
            return false;
        }
    };
    AuthService.prototype.login$ = function (userName, password) {
        var _this = this;
        var header = new http_1.HttpHeaders().set('Content-Type', 'application/json');
        var body = JSON.stringify({ "Username": userName, "Password": password });
        var options = { headers: header };
        return this.http.put("/api/TokenAuth/Login", body, options).pipe(operators_1.debounceTime(200), operators_1.distinctUntilChanged(), operators_1.map(function (res) {
            var result = res;
            if (result.state && result.state == 1 && result.data && result.data.accessToken) {
                sessionStorage.setItem(_this.tokeyKey, result.data.accessToken);
            }
            return result;
        }), operators_1.catchError(this.handleError("login")));
    };
    AuthService.prototype.authGet$ = function (url) {
        var header = this.initAuthHeaders();
        var options = { headers: header };
        return this.http.get(url, options).pipe(operators_1.debounceTime(200), operators_1.distinctUntilChanged(), operators_1.catchError(this.handleError("authGet")));
    };
    AuthService.prototype.checkLogin = function () {
        var token = sessionStorage.getItem(this.tokeyKey);
        return token != null;
    };
    AuthService.prototype.getUserInfo$ = function () {
        return this.authGet$("/api/TokenAuth");
    };
    AuthService.prototype.authPost$ = function (url, body) {
        var headers = this.initAuthHeaders();
        return this.http.post(url, body, { headers: headers }).pipe(operators_1.debounceTime(200), operators_1.distinctUntilChanged(), operators_1.catchError(this.handleError("authPost")));
    };
    AuthService.prototype.getLocalToken = function () {
        return sessionStorage.getItem(this.tokeyKey);
    };
    AuthService.prototype.initAuthHeaders = function () {
        var token = this.getLocalToken();
        if (token == null)
            throw "No token";
        var headers = new http_1.HttpHeaders()
            .set('Content-Type', 'application/json')
            .set("Authorization", "Bearer " + token);
        return headers;
    };
    AuthService.prototype.handleError = function (operation, result) {
        if (operation === void 0) { operation = 'operation'; }
        return function (error) {
            console.error(operation + " error: " + error.message);
            return of_1.of(result);
        };
    };
    AuthService = __decorate([
        core_1.Injectable(),
        __metadata("design:paramtypes", [http_1.HttpClient, router_1.Router])
    ], AuthService);
    return AuthService;
}());
exports.AuthService = AuthService;
//# sourceMappingURL=auth.service.js.map