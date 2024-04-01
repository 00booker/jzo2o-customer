package com.jzo2o.customer.controller.open;


import com.jzo2o.customer.model.dto.request.InstitutionRegisterReqDTO;
import com.jzo2o.customer.model.dto.request.InstitutionResetPasswordReqDTO;
import com.jzo2o.customer.service.IInstitutionStaffService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


@RequestMapping("/open/serve-provider/institution")
@Api(tags = "白名单接口 - 机构注册相关接口")
@RestController("openRegisterController")
public class RegisterController {
    @Resource
    IInstitutionStaffService iInstitutionStaffService;

    @PostMapping("/register")
    @ApiOperation(value = "机构人员注册相关接口")
    public void institutionRegister(@RequestBody InstitutionRegisterReqDTO institutionRegisterReqDTO) {
        iInstitutionStaffService.institutionRegister(institutionRegisterReqDTO);
    }

    @PostMapping("/resetPassword")
    @ApiOperation(value = "机构人员重置密码相关接口")
    public void institutionResetPassword(@RequestBody InstitutionResetPasswordReqDTO institutionResetPasswordReqDTO) {
        iInstitutionStaffService.institutionResetPassword(institutionResetPasswordReqDTO);
    }
}
