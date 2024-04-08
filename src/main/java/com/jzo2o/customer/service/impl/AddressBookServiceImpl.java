package com.jzo2o.customer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jzo2o.api.customer.dto.response.AddressBookResDTO;
import com.jzo2o.common.expcetions.ForbiddenOperationException;
import com.jzo2o.common.model.CurrentUserInfo;
import com.jzo2o.common.model.PageResult;
import com.jzo2o.common.model.dto.PageQueryDTO;
import com.jzo2o.common.utils.BeanUtils;
import com.jzo2o.common.utils.CollUtils;
import com.jzo2o.customer.mapper.AddressBookMapper;
import com.jzo2o.customer.model.domain.AddressBook;
import com.jzo2o.customer.model.dto.request.AddressBookUpsertReqDTO;
import com.jzo2o.customer.service.IAddressBookService;
import com.jzo2o.mvc.utils.UserContext;
import com.jzo2o.mysql.utils.PageUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * <p>
 * 地址薄 服务实现类
 * </p>
 *
 * @author itcast
 * @since 2023-07-06
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements IAddressBookService {

    @Override
    public List<AddressBookResDTO> getByUserIdAndCity(Long userId, String city) {

        List<AddressBook> addressBooks = lambdaQuery()
                .eq(AddressBook::getUserId, userId)
                .eq(AddressBook::getCity, city)
                .list();
        if(CollUtils.isEmpty(addressBooks)) {
            return new ArrayList<>();
        }
        return BeanUtils.copyToList(addressBooks, AddressBookResDTO.class);
    }

    @Override
    public void addAddressBook(AddressBookUpsertReqDTO addressBookUpsertReqDTO) {
        String[] s = new String[2];
        CurrentUserInfo currentUserInfo = UserContext.currentUser();
        AddressBook addressBook = new AddressBook().setUserId(currentUserInfo.getId());
        if (addressBookUpsertReqDTO.getName() == null) {
            throw new ForbiddenOperationException("名称不能为空");
        }
        addressBook.setName(addressBookUpsertReqDTO.getName());
        if (addressBookUpsertReqDTO.getAddress() == null) {
            throw new ForbiddenOperationException("详细不能为空");
        }
        addressBook.setAddress(addressBookUpsertReqDTO.getAddress());
        if (addressBookUpsertReqDTO.getPhone() == null) {
            throw new ForbiddenOperationException("电话不能为空");
        }
        addressBook.setPhone(addressBookUpsertReqDTO.getPhone());
        if (addressBookUpsertReqDTO.getProvince() == null) {
            throw new ForbiddenOperationException("省份不能为空");
        }
        addressBook.setProvince(addressBookUpsertReqDTO.getProvince());
        if (addressBookUpsertReqDTO.getCity() == null) {
            throw new ForbiddenOperationException("城市不能为空");
        }
        addressBook.setCity(addressBookUpsertReqDTO.getCity());
        if (addressBookUpsertReqDTO.getCounty() == null) {
            throw new ForbiddenOperationException("地区不能为空");
        }
        addressBook.setCounty(addressBookUpsertReqDTO.getCounty());
         String local = addressBookUpsertReqDTO.getLocation();
        if (local != null) {
            String[] adressSegments = local.split(",");
            addressBook.setLon(Double.valueOf(adressSegments[0]));
            addressBook.setLat(Double.valueOf(adressSegments[1]));
        }
        addressBook.setIsDefault(addressBookUpsertReqDTO.getIsDefault());

        super.save(addressBook);
    }

    @Override
    public PageResult<AddressBookResDTO> pageQueryAddressBook(PageQueryDTO pageQueryDTO) {
        CurrentUserInfo currentUserInfo = UserContext.currentUser();
        Long currentUserId = UserContext.currentUserId();
        Page<AddressBook> addressBookPage = PageUtils.parsePageQuery(pageQueryDTO, AddressBook.class);
        // 创建QueryWrapper并添加用户ID匹配条件
        QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", currentUserInfo.getId());
        // Page<AddressBook> addressBookPage1 = baseMapper.selectPage(addressBookPage, new QueryWrapper<>().eq("user_id", currentUserId));
        Page<AddressBook> addressBookPage1 = baseMapper.selectPage(addressBookPage, queryWrapper);
        return PageUtils.toPage(addressBookPage1, AddressBookResDTO.class);
    }

    @Override
    public AddressBookResDTO getAddressBookById(Long id) {
        if (id == null) {
            throw new ForbiddenOperationException("id不能为空");
        }
        // 简化后的代码
        AddressBook byId = Optional.ofNullable(super.getById(id))
                .orElseThrow(() -> new ForbiddenOperationException("Address book not found for ID: " + id));
        AddressBookResDTO addressBookResDTO = BeanUtils.copyProperties(byId, AddressBookResDTO.class);
        return addressBookResDTO;
    }

    @Override
    public void updateById(Long id, @RequestBody AddressBookUpsertReqDTO addressBookUpsertReqDTO){
        if (id == null) {
            throw new ForbiddenOperationException("id不能为空");
        }
        // 简化后的代码
        AddressBook byId = Optional.ofNullable(super.getById(id))
                .orElseThrow(() -> new ForbiddenOperationException("Address book not found for ID: " + id));

        AddressBook addressBook = BeanUtils.copyProperties(addressBookUpsertReqDTO, AddressBook.class);
        String local = addressBookUpsertReqDTO.getLocation();
        if (local != null) {
            String[] adressSegments = local.split(",");
            addressBook.setLon(Double.valueOf(adressSegments[0]));
            addressBook.setLat(Double.valueOf(adressSegments[1]));
        }
        addressBook.setId(id);
        super.updateById(addressBook);
    }

    @Override
    public void removeAddressBookByIds(String[] ids) {
        if (ids.length <= 0) {
            throw new ForbiddenOperationException("id不能为空");
        }
        List<Long> longIds = new ArrayList<>();
        for (String id : ids) {
            try {
                long longId = Long.parseLong(id);
                longIds.add(longId);
            } catch (NumberFormatException e) {
                throw new ForbiddenOperationException("Invalid ID format:" + id);
            }
        }

        super.removeByIds(longIds);
    }

    @Override
    public void setDefault(Integer flag, Long id) {
        if (id == null) {
            throw new ForbiddenOperationException("id不能为空");
        }
        if (flag == null) {
            throw new ForbiddenOperationException("flag不能为空");
        }
        AddressBook addressBook = Optional.ofNullable(super.getById(id))
                .orElseThrow(() -> new ForbiddenOperationException("Address book not found for ID: " + id));
        if (flag == 1) {
            lambdaUpdate()
                    .eq(AddressBook::getUserId, UserContext.currentUserId())
                    .eq(AddressBook::getIsDefault, 1)
                    .set(AddressBook::getIsDefault, 0)
                    .update();
        }
        addressBook.setIsDefault(flag);
        super.updateById(addressBook);
    }

    @Override
    public AddressBookResDTO getDefaultAddress() {
        // // 创建QueryWrapper并添加用户ID匹配条件
        // QueryWrapper<AddressBook> queryWrapper = new QueryWrapper<>();
        // queryWrapper.eq("user_id", currentUserInfo.getId());
        AddressBook defaultAddress = lambdaQuery()
                .eq(AddressBook::getUserId, UserContext.currentUserId())
                .eq(AddressBook::getIsDefault, 1)
                .one();
        return BeanUtils.copyProperties(defaultAddress, AddressBookResDTO.class);
    }
}
