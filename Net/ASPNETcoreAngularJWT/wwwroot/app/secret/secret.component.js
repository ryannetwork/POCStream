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
var auth_service_1 = require("../shared/auth.service");
var SecretComponent = /** @class */ (function () {
    function SecretComponent(authService) {
        this.authService = authService;
    }
    SecretComponent.prototype.ngOnInit = function () {
        var _this = this;
        this.authService.getUserInfo$().subscribe(function (res) {
            if (res != null && res.data) {
                var thisuser = res.data;
                if (thisuser && thisuser.userName) {
                    _this.userName = thisuser.userName;
                }
            }
        });
    };
    SecretComponent = __decorate([
        core_1.Component({
            template: "\n        <div class=\"card-wide mdl-card mdl-shadow--2dp\">\n            <div class=\"mdl-card__title\">\n                <h2 class=\"mdl-card__title-text\">Secret</h2>\n            </div>\n            <div class=\"mdl-card__supporting-text\">\n                You can get source code here: https://github.com/Longfld/ASPNETcoreAngularJWT\n            </div>\n            <div class=\"mdl-card__supporting-text\">\n                 <h1>Hi <span>{{userName}}</span></h1>\n            </div>\n        </div>\n    "
        }),
        __metadata("design:paramtypes", [auth_service_1.AuthService])
    ], SecretComponent);
    return SecretComponent;
}());
exports.SecretComponent = SecretComponent;
//# sourceMappingURL=secret.component.js.map