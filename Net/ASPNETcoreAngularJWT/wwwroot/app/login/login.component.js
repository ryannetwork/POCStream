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
var router_1 = require("@angular/router");
var auth_service_1 = require("../shared/auth.service");
var LoginComponent = /** @class */ (function () {
    function LoginComponent(authService, router) {
        this.authService = authService;
        this.router = router;
    }
    LoginComponent.prototype.login = function () {
        var _this = this;
        if (this.postStream$) {
            this.postStream$.unsubscribe;
        }
        this.postStream$ = this.authService.login$(this.userName, this.password).subscribe(function (result) {
            if (result.state == 1) {
                _this.router.navigate(["home"]);
            }
            else {
                alert(result.msg);
            }
        });
    };
    LoginComponent.prototype.ngOnDestroy = function () {
        if (this.postStream$) {
            this.postStream$.unsubscribe();
        }
    };
    LoginComponent = __decorate([
        core_1.Component({
            moduleId: module.id,
            selector: "my-login",
            template: "\n        <div class=\"card-wide mdl-card mdl-shadow--2dp\">\n            <div class=\"mdl-card__title\">\n                <h2 class=\"mdl-card__title-text\">login</h2>\n            </div>\n        </div>\n          <div class=\"card-wide mdl-card mdl-shadow--2dp\">\n          <table>\n    <tr>\n        <td>user name:</td>\n        <td><input [(ngModel)]=\"userName\" placeholder=\"user1\" matTooltip=\"Enter User Name\" matTooltipPosition=\"right\" /></td>\n    </tr>\n    <tr>\n        <td>password:</td>\n        <td><input [(ngModel)]=\"password\" placeholder=\"user1psd\"  matTooltip=\"Enter Password\" matTooltipPosition=\"right\" /></td>\n    </tr>\n    <tr>\n        <td></td>\n        <td><button mat-raised-button color=\"primary\"  (click)=\"login()\" >Login</button></td>\n    </tr>\n</table>\n          </div>\n    "
        }),
        __metadata("design:paramtypes", [auth_service_1.AuthService, router_1.Router])
    ], LoginComponent);
    return LoginComponent;
}());
exports.LoginComponent = LoginComponent;
//# sourceMappingURL=login.component.js.map