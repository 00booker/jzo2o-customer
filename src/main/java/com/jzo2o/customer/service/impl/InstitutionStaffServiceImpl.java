package com.jzo2o.customer.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.api.customer.dto.request.InstitutionStaffAddReqDTO;
import com.jzo2o.api.customer.dto.request.InstitutionStaffPageQueryReqDTO;
import com.jzo2o.api.customer.dto.response.InstitutionStaffResDTO;
import com.jzo2o.api.orders.OrdersServeApi;
import com.jzo2o.api.orders.dto.response.InstitutionStaffServeCountResDTO;
import com.jzo2o.api.publics.SmsCodeApi;
import com.jzo2o.common.constants.CommonStatusConstants;
import com.jzo2o.common.constants.ErrorInfo;
import com.jzo2o.common.constants.UserType;
import com.jzo2o.common.enums.SmsBussinessTypeEnum;
import com.jzo2o.common.expcetions.BadRequestException;
import com.jzo2o.common.expcetions.CommonException;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.common.utils.StringUtils;
import com.jzo2o.customer.mapper.InstitutionStaffMapper;
import com.jzo2o.customer.model.domain.InstitutionStaff;
import com.jzo2o.customer.model.domain.ServeProvider;
import com.jzo2o.customer.model.dto.request.InstitutionRegisterReqDTO;
import com.jzo2o.customer.model.dto.request.InstitutionResetPasswordReqDTO;
import com.jzo2o.customer.model.dto.request.InstitutionStaffUpsertReqDTO;
import com.jzo2o.customer.model.dto.response.InstitutionStaffSimpleResDTO;
import com.jzo2o.customer.service.IInstitutionStaffService;
import com.jzo2o.customer.service.IServeProviderService;
import com.jzo2o.mvc.utils.UserContext;
import com.jzo2o.mysql.utils.PageUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 机构下属服务人员 服务实现类
 * </p>
 *
 * @author itcast
 * @since 2023-07-18
 */
@Service
public class InstitutionStaffServiceImpl extends ServiceImpl<InstitutionStaffMapper, InstitutionStaff> implements IInstitutionStaffService {
    @Resource
    private OrdersServeApi ordersServeApi;

    @Resource
    private SmsCodeApi smsCodeApi;

    @Resource
    private IServeProviderService serveProviderService;

    /**
     * 新增机构下属服务人员
     *
     * @param institutionStaffUpsertReqDTO 插入更新机构下属服务人员
     */
    @Override
    public void add(InstitutionStaffUpsertReqDTO institutionStaffUpsertReqDTO) {
        InstitutionStaff institutionStaff = BeanUtil.toBean(institutionStaffUpsertReqDTO, InstitutionStaff.class);
        institutionStaff.setCertificationImgs(institutionStaffUpsertReqDTO.getCertificationImgs());
        institutionStaff.setInstitutionId(UserContext.currentUserId());
        institutionStaff.setCode(IdUtil.getSnowflakeNextIdStr());
        baseMapper.insert(institutionStaff);
    }

    @Override
    public void add(InstitutionStaffAddReqDTO institutionStaffAddReqDTO) {
        InstitutionStaff institutionStaff = BeanUtil.toBean(institutionStaffAddReqDTO, InstitutionStaff.class);
        institutionStaff.setCode(IdUtil.getSnowflakeNextIdStr());
        baseMapper.insert(institutionStaff);
    }

    /**
     * 更新机构下属服务人员
     *
     * @param id                           机构下属服务人员id
     * @param institutionStaffUpsertReqDTO 插入更新机构下属服务人员
     */
    @Override
    public void update(Long id, InstitutionStaffUpsertReqDTO institutionStaffUpsertReqDTO) {
        InstitutionStaff institutionStaff = BeanUtil.toBean(institutionStaffUpsertReqDTO, InstitutionStaff.class);
        institutionStaff.setCertificationImgs(institutionStaffUpsertReqDTO.getCertificationImgs());
        institutionStaff.setInstitutionId(UserContext.currentUserId());
        institutionStaff.setId(id);
        baseMapper.updateById(institutionStaff);
    }

    /**
     * 分页查询
     *
     * @param institutionStaffPageQueryReqDTO 分页查询请求
     * @return 分页结果
     */
    @Override
    public PageResult<InstitutionStaffResDTO> pageQuery(InstitutionStaffPageQueryReqDTO institutionStaffPageQueryReqDTO) {
        Page<InstitutionStaff> page = PageUtils.parsePageQuery(institutionStaffPageQueryReqDTO, InstitutionStaff.class);
        LambdaQueryWrapper<InstitutionStaff> queryWrapper = Wrappers.<InstitutionStaff>lambdaQuery()
                .eq(InstitutionStaff::getInstitutionId, institutionStaffPageQueryReqDTO.getInstitutionId())
                .eq(ObjectUtil.isNotEmpty(institutionStaffPageQueryReqDTO.getName()), InstitutionStaff::getName, institutionStaffPageQueryReqDTO.getName())
                .eq(ObjectUtil.isNotEmpty(institutionStaffPageQueryReqDTO.getPhone()), InstitutionStaff::getPhone, institutionStaffPageQueryReqDTO.getPhone());
        Page<InstitutionStaff> serveTypePage = baseMapper.selectPage(page, queryWrapper);

        return PageUtils.toPage(serveTypePage, InstitutionStaffResDTO.class, (entity, dto) -> {
            if (ObjectUtil.isNotEmpty(entity.getCertificationImgs())) {
                dto.setCertificationImgs(entity.getCertificationImgs());
            } else {
                dto.setCertificationImgs(Collections.emptyList());
            }
        });
    }

    /**
     * 获取机构下属服务人员简略列表
     *
     * @return 服务人员简略列表
     */
    @Override
    public List<InstitutionStaffSimpleResDTO> queryInstitutionStaffList() {
        LambdaQueryWrapper<InstitutionStaff> queryWrapper = Wrappers.<InstitutionStaff>lambdaQuery()
                .eq(InstitutionStaff::getInstitutionId, UserContext.currentUserId())
                .orderByDesc(InstitutionStaff::getCreateTime);
        return BeanUtil.copyToList(baseMapper.selectList(queryWrapper), InstitutionStaffSimpleResDTO.class);
    }

    /**
     * 删除服务人员
     *
     * @param id 服务人员id
     */
    @Override
    public void delete(Long id) {
        //校验服务人员是否有关联服务单
        InstitutionStaffServeCountResDTO institutionStaffServeCountResDTO = ordersServeApi.countByInstitutionStaffId(id);
        if (institutionStaffServeCountResDTO.getCount() > 0) {
            throw new ForbiddenOperationException("该服务人员有关联的服务，不可删除");
        }

        //删除机构下服务人员
        baseMapper.deleteById(id);
    }

    @Override
    public InstitutionStaffResDTO findByIdAndInstitutionId(Long id, Long institutionId) {
        InstitutionStaff institutionStaff = lambdaQuery().eq(InstitutionStaff::getId, id)
                .eq(InstitutionStaff::getInstitutionId, institutionId)
                .one();
        return BeanUtils.toBean(institutionStaff, InstitutionStaffResDTO.class);
    }

    @Override
    public List<InstitutionStaffResDTO> findByInstitutionId(Long institutionId) {

        List<InstitutionStaff> list = lambdaQuery()
                .eq(InstitutionStaff::getInstitutionId, institutionId)
                .list();
        return BeanUtils.copyToList(list, InstitutionStaffResDTO.class);
    }

    @Override
    public void institutionRegister(InstitutionRegisterReqDTO institutionRegisterReqDTO) {
        if(StringUtils.isEmpty(institutionRegisterReqDTO.getVerifyCode())){
            throw new BadRequestException("验证码错误，请重新获取");
        }
        //远程调用publics服务校验验证码是否正确
        boolean verifyResult = smsCodeApi.verify(institutionRegisterReqDTO.getPhone(), SmsBussinessTypeEnum.INSTITION_REGISTER, institutionRegisterReqDTO.getVerifyCode()).getIsSuccess();
        if(!verifyResult) {
            throw new BadRequestException("验证码错误，请重新获取");
        }
        ServeProvider serveProvider = serveProviderService.findByPhoneAndType(institutionRegisterReqDTO.getPhone(), UserType.INSTITUTION);
        // 账号禁用校验
        if(serveProvider != null) {
            throw new CommonException("账户已注册");
        }
        if(serveProvider != null && CommonStatusConstants.USER_STATUS_FREEZE == serveProvider.getStatus()) {
            throw new CommonException(ErrorInfo.Code.ACCOUNT_FREEZED, serveProvider.getAccountLockReason());
        }
        // 自动注册
        if(serveProvider == null) {
            serveProvider = serveProviderService.add(institutionRegisterReqDTO.getPhone(), UserType.INSTITUTION, institutionRegisterReqDTO.getPassword());
        }
    }

    @Override
    public void institutionResetPassword(InstitutionResetPasswordReqDTO institutionResetPasswordReqDTO) {
        if(StringUtils.isEmpty(institutionResetPasswordReqDTO.getVerifyCode())){
            throw new BadRequestException("验证码错误，请重新获取");
        }
        //远程调用publics服务校验验证码是否正确
        boolean verifyResult = smsCodeApi.verify(institutionResetPasswordReqDTO.getPhone(), SmsBussinessTypeEnum.INSTITION_REGISTER, institutionResetPasswordReqDTO.getVerifyCode()).getIsSuccess();
        if(!verifyResult) {
            throw new BadRequestException("验证码错误，请重新获取");
        }
        ServeProvider serveProvider = serveProviderService.findByPhoneAndType(institutionResetPasswordReqDTO.getPhone(), UserType.INSTITUTION);
        if(serveProvider != null && CommonStatusConstants.USER_STATUS_FREEZE == serveProvider.getStatus()) {
            throw new CommonException(ErrorInfo.Code.ACCOUNT_FREEZED, serveProvider.getAccountLockReason());
        }
        // 自动注册
        if(serveProvider == null) {
            serveProvider = serveProviderService.add(institutionResetPasswordReqDTO.getPhone(), UserType.INSTITUTION, institutionResetPasswordReqDTO.getPassword());
        }

    }
}
